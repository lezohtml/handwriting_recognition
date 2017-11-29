package com.example.etudiant.interfacesaisie;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class MainActivity extends Activity implements View.OnClickListener{
	private Button bChoice1;
	private Button bChoice2;
	private Button bChoice3;
	private Button bSave;
	private Button bReset;
	private Button bCancelChoice;
	private DrawingView mDrawingView;
    private TextView texteDescription;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutmain);
		initializeUI();
		setListeners();
		ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},101);
	}

	// Création des objets représentant la zone de dessin, la zone de texte et les boutons de sauvegarde et de remise à zéro
	private void initializeUI() {
		mDrawingView = (DrawingView) findViewById(R.id.scratch_pad);
		bChoice1 = (Button) findViewById(R.id.choice_1);
		bChoice2 = (Button) findViewById(R.id.choice_2);
		bChoice3 = (Button) findViewById(R.id.choice_3);
		bSave = (Button) findViewById(R.id.save_button);
		bReset = (Button) findViewById(R.id.reset_button);
		bCancelChoice = (Button) findViewById(R.id.annulerChoix);
		bCancelChoice.setVisibility(View.GONE);
        texteDescription = findViewById(R.id.texteDescription);

		bChoice1.setPaintFlags(bChoice1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		bChoice1.setVisibility(View.GONE);
		bChoice2.setPaintFlags(bChoice2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		bChoice2.setVisibility(View.GONE);
		bChoice3.setPaintFlags(bChoice3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		bChoice3.setVisibility(View.GONE);
	}

	// Création des événements d'écoute une action sur un bouton
	private void setListeners() {
		bChoice1.setOnClickListener(this);
		bChoice2.setOnClickListener(this);
		bChoice3.setOnClickListener(this);
		bSave.setOnClickListener(this);
		bReset.setOnClickListener(this);
		bCancelChoice.setOnClickListener(this);

	}

	// Que faire si on appuie sur un bouton
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			// appuie sur le bouton de proposition 1
			case R.id.choice_1:
				this.hide_choice_button(0);  //Met à jour l'application en sachant que un choix a été fait
				break;

			// appuie sur le bouton de proposition 2
			case R.id.choice_2:
				this.hide_choice_button(0); //Met à jour l'application en sachant que un choix a été fait
				break;

			// appuie sur le bouton de proposition 3
			case R.id.choice_3:
				this.hide_choice_button(0); //Met à jour l'application en sachant que un choix a été fait
				break;

			// appuie sur le bouton de sauvegarde
			case R.id.save_button:
			    if(mDrawingView.dessinEmpty()){
                    Toast.makeText(getApplicationContext(), "Veuilez ecrire votre mot", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        envoiImage(view);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
				break;

			// appuie sur le bouton de remise à zéro
			case R.id.reset_button:
				mDrawingView.reset();
				break;
			case R.id.annulerChoix: //Met à jour l'application en sachant qu'il y a eu une annulation
				this.hide_choice_button(1);

		}
	}

	//Envoi de l'image vers le serveur : Transformation de la DrawView en Bitmap puis en String pour l'envoi avec un Json
	public void envoiImage(View view) throws JSONException {

		//Creation d'un String à partir du bitmap pour le preparer à l'envoi
		Bitmap bitmapEnvoi = mDrawingView.getBitmap();
		final int COMPRESSION_QUALITY = 100;
		String encodedImage;
		ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
		bitmapEnvoi.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, byteArrayBitmapStream);
		byte[] b = byteArrayBitmapStream.toByteArray();
		encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

		//Préparation du JSON avec l'image
		JSONObject myJson = new JSONObject();
		myJson.put("img", encodedImage);
		myJson.put("label","label");

		//Preparation de la requete du JSON à l'adresse 'url'
		String url = "http://tf.boblecodeur.fr:8000/postimg";
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, myJson, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							response.getString("mot");
							//switch(response.getInt("nbResultat")){-----------------------
							switch(1){  //On affiche le nombre de résultat en fonction du nombre de choix que propose le serveur
								case 0:
									Toast.makeText(getApplicationContext(), "Le mot n'a pas été trouvé", Toast.LENGTH_SHORT).show();
									break;
								case 3:
									bChoice3.setVisibility(Button.VISIBLE);
								case 2:
									bChoice2.setVisibility(Button.VISIBLE);
								case 1:
									bChoice1.setVisibility(Button.VISIBLE);
									bSave.setVisibility(EditText.GONE);
									mDrawingView.hidemPaint();
									bReset.setVisibility(View.GONE);
                                    texteDescription.setVisibility(View.GONE);
									bCancelChoice.setVisibility(View.VISIBLE);
                                    Toast toast= Toast.makeText(getApplicationContext(),"Ceci est il votre mot ?", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.DISPLAY_CLIP_VERTICAL|Gravity.CENTER_HORIZONTAL, 1, 1);
                                    toast.show();
                                    bChoice1.setText(response.getString("mot"));
									break;
								default:
									break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), "Erreur de connection avec le serveur", Toast.LENGTH_SHORT).show();

						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
						Toast.makeText(getApplicationContext(), "Erreur dans l'envoi", Toast.LENGTH_SHORT).show();
					}
				});

		//Envoi de la requete préparée à l'étape précédente
		MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
	}


	// Lorsque le l'utilisateur clique sur l'envoi: Cache les boutons de proposition de mots et change leurs labels
	public void hide_choice_button(int button) {
		bChoice1.setVisibility(Button.GONE);
		bChoice2.setVisibility(Button.GONE);
		bChoice3.setVisibility(Button.GONE);
		bCancelChoice.setVisibility(View.GONE);
		bSave.setVisibility(EditText.VISIBLE);
		bReset.setVisibility(EditText.VISIBLE);
        texteDescription.setVisibility(View.VISIBLE);
        mDrawingView.showmPaint();

		//Si l'utilisateur annule on ne lui supprime pas son image
		if(button==0) {  //Si l'utilisateur à fait un choix dans ce qui sont proposé ; on supprime le dessin
            resetAppuiBouton();
		}

	}


	public void resetAppuiBouton(){
        mDrawingView.reset();
    }
}