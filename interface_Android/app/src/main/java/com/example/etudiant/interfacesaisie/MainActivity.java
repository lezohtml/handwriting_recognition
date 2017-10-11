package com.example.etudiant.interfacesaisie;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
	private String path=Environment.getExternalStorageDirectory().toString()+File.separator.toString()+"interface_android";
	//private File folder;

	private EditText mEdit;
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
		mEdit = (EditText)findViewById(R.id.editText);
		mSaveButton = (Button) findViewById(R.id.save_button);
		ResetButton = (Button) findViewById(R.id.reset_button);
	}

	// Création des événements d'écoute une action sur un bouton
	private void setListeners() {
		mSaveButton.setOnClickListener(this);
		ResetButton.setOnClickListener(this);
	}

	// Que faire si on appuie sur un bouton
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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

	// Envoi de l'image vers le serveur : Transformation de la DrawView en Bitmap puis en String pour l'envoi avec un Json
    public void envoiImage(View view) throws JSONException {
		final EditText mTxtDisplay = (EditText) findViewById(R.id.editText);
		String url = "http://tf.boblecodeur.fr:8000/postimg";
		JSONObject myJson = new JSONObject();


		// Creation d'un String à partir du bitmap pour le preparer à l'envoi
		Bitmap bitmapEnvoi = mDrawingView.getBitmap();
		final int COMPRESSION_QUALITY = 100;
		String encodedImage;
		ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
		bitmapEnvoi.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,byteArrayBitmapStream);
		byte[] b = byteArrayBitmapStream.toByteArray();
		encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


		myJson.put("img","test");
		myJson.put("label","test");


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
				(Request.Method.POST, url, myJson, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						mTxtDisplay.setText("Response: " + response.toString());
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mTxtDisplay.setText("Error: " + error.toString());

					}
				});


		// Access the RequestQueue through your singleton class.
		MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

	}

	// Remplace le Canva, son Bitmap et vide la zone de texte
	public void reset() {
		mDrawingView.reset();
		mEdit.setText("");
	}
}