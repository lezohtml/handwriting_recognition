package com.example.etudiant.interfacesaisie;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by etudiant on 04/10/17.
 */

public class MainActivity extends Activity implements View.OnClickListener{
	//private String path=Environment.getExternalStorageDirectory().toString()+File.separator.toString()+"interface_android";
	//private File folder;

	private EditText mEdit;
	private Button choice_1;
	private Button choice_2;
	private Button choice_3;
	private Button mSaveButton;
	private Button ResetButton;

	private DrawingView mDrawingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutmain);

		initializeUI();
		setListeners();

		//folder = new File(path);


        ActivityCompat.requestPermissions(this,new String[]{/*Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,*/Manifest.permission.INTERNET},101);
    }

	// Création des objets représentant la zone de dessin, la zone de texte et les boutons de sauvegarde et de remise à zéro
	private void initializeUI() {
		mDrawingView = (DrawingView) findViewById(R.id.scratch_pad);
		choice_1 = (Button) findViewById(R.id.choice_1);
		choice_2 = (Button) findViewById(R.id.choice_2);
		choice_3 = (Button) findViewById(R.id.choice_3);
		mEdit = (EditText)findViewById(R.id.editText);
		mSaveButton = (Button) findViewById(R.id.save_button);
		ResetButton = (Button) findViewById(R.id.reset_button);

		choice_1.setPaintFlags(choice_1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		choice_1.setVisibility(View.GONE);
		choice_2.setPaintFlags(choice_2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		choice_2.setVisibility(View.GONE);
		choice_3.setPaintFlags(choice_3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		choice_3.setVisibility(View.GONE);


	}

	// Création des événements d'écoute une action sur un bouton
	private void setListeners() {
		choice_1.setOnClickListener(this);
		choice_2.setOnClickListener(this);
		choice_3.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);
		ResetButton.setOnClickListener(this);
	}

	// Que faire si on appuie sur un bouton
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
			// appuie sur le bouton de proposition 1
			case R.id.choice_1:
				 Toast.makeText(this, "choix 1", Toast.LENGTH_LONG).show();
				this.hide_choice_button();
			break;

			// appuie sur le bouton de proposition 2
			case R.id.choice_2:
				Toast.makeText(this, "choix 2", Toast.LENGTH_LONG).show();
				this.hide_choice_button();
				break;

			// appuie sur le bouton de proposition 3
			case R.id.choice_3:
				Toast.makeText(this, "choix 3", Toast.LENGTH_LONG).show();
				this.hide_choice_button();
				break;

			// appuie sur le bouton de sauvegarde
			case R.id.save_button:
				// si la zone de texte est vide
				if(mEdit.getText().toString().matches("")) {
					// alors message d'erreur et focus sur la zone de texte
					Toast.makeText(this, R.string.name_field_empty, Toast.LENGTH_SHORT).show();
					mEdit.requestFocus();
				} else {
					//boolean success = true;
					//if (!folder.exists()) {
						//success = folder.mkdirs();
					//}
					//if (success) {
						//try {
							//mDrawingView.saveImage(path, mEdit.getText().toString(), Bitmap.CompressFormat.PNG, 100);
							//Toast.makeText(this, this.getResources().getString(R.string.save_success_1) + mEdit.getText().toString() + this.getResources().getString(R.string.save_success_2) + path, Toast.LENGTH_LONG).show();
						//} catch (Exception e) {
							//Toast.makeText(this, this.getResources().getString(R.string.error_file) + mEdit.getText().toString() + this.getResources().getString(R.string.save_success_2)+ path + " !", Toast.LENGTH_LONG).show();
						//}
					//} else {
						//Toast.makeText(this, this.getResources().getString(R.string.error_folder) + path + " !", Toast.LENGTH_LONG).show();
					//}

					try {
						envoiImage(view);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			break;

			// appuie sur le bouton de remise à zéro
			case R.id.reset_button:
				reset();
			break;
        }
    }

	//Envoi de l'image vers le serveur : Transformation de la DrawView en Bitmap puis en String pour l'envoi avec un Json
	public void envoiImage(View view) throws JSONException {
		final EditText mTxtDisplay = (EditText) findViewById(R.id.editText);
		String url = "http://tf.boblecodeur.fr:8000/postimg";
		JSONObject myJson = new JSONObject();

		//Creation d'un String à partir du bitmap pour le preparer à l'envoi
		Bitmap bitmapEnvoi = mDrawingView.getBitmap();
		final int COMPRESSION_QUALITY = 100;
		String encodedImage;
		ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
		bitmapEnvoi.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, byteArrayBitmapStream);
		byte[] b = byteArrayBitmapStream.toByteArray();
		encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


		myJson.put("img", encodedImage);
		myJson.put("label", mTxtDisplay.getText().toString());
        Toast.makeText(getApplicationContext(), mTxtDisplay.getText().toString(), Toast.LENGTH_SHORT).show();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest
				(Request.Method.POST, url, myJson, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							mTxtDisplay.setText("Response: " + response.getString("return"));
							show_choice_button();


                        } catch (JSONException e) {
							e.printStackTrace();
                            mTxtDisplay.setText("Error: " + e.toString());

                        }
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
						mTxtDisplay.setText("Error: " + error.toString());
                        Toast.makeText(getApplicationContext(), "Erreur dans l'envoi", Toast.LENGTH_SHORT).show();


                    }
				});

		// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
	}

	// Remplace le Canva, son Bitmap et vide la zone de texte
	public void reset() {
		mDrawingView.reset();
		hide_choice_button();
		mEdit.setText("");
	}

	// Lorsque le l'utilisateur clique sur l'envoi: Cache les boutons de proposition de mots et change les labels
	public void hide_choice_button() {
		choice_1.setVisibility(Button.GONE);
		choice_2.setVisibility(Button.GONE);
		choice_3.setVisibility(Button.GONE);
		mEdit.setVisibility(EditText.VISIBLE);
		Button saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setVisibility(EditText.VISIBLE);
		Button myButton = (Button) findViewById(R.id.reset_button);
		myButton.setText("Effacer le mot");
		mDrawingView.showmPaint();
		//reset();
		//	findViewById(R.id.mainLayout).invalidate();
	}

	// Inverse hide_choice : Affiche tout les boutons pour le choix et change les labels pour correspondra à létat de l'application
	public void show_choice_button() {
		choice_1.setVisibility(Button.VISIBLE);
		choice_2.setVisibility(Button.VISIBLE);
		choice_3.setVisibility(Button.VISIBLE);
		mEdit.setVisibility(EditText.GONE);
		Button saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setVisibility(EditText.GONE);
		Button resetButton = (Button) findViewById(R.id.reset_button);
		resetButton.setText("Annuler le choix");
		mDrawingView.hidemPaint();
	}
}