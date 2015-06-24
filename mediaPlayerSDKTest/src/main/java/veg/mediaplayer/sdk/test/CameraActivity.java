package veg.mediaplayer.sdk.test;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import EQuicamApp.R;

/**
 * Created by Equifilm on 16-6-2015.
 */
public class CameraActivity extends Activity {

//    private static final String         defaultUrl = "rtsp://74.106.249.39:554";
    private static final String         defaultUrl = "default URL";

    public  static String               currentCameraUrl;
    private EditText                    cameraURLeditText;
    private EditText                    poortEditText;
    private EditText                    gebruikersNaamET;
    private EditText                    wwET;


//    //Sharedprefs
//    private static final int 			            PREFERENCE_MODE_PRIVATE = 0;
//    private static SharedPreferences                sharedPrefs;
//    private static SharedPreferences.Editor 	    sharedPrefsEditor;

    //Oncreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Camera");
        setContentView(R.layout.camera);

        //setkeyboard to appear directly
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //get buttons
        cameraURLeditText = (EditText) findViewById(R.id.cameraURLeditText);
        poortEditText = (EditText) findViewById(R.id.poortEditText);
        gebruikersNaamET = (EditText) findViewById(R.id.gebruikersNaamET);
        wwET = (EditText) findViewById(R.id.wwET);
    }

    //onclicklistener submit
    public void onSubmit(View view){

        //alle info opslaan
        String tmpCameraUrl = cameraURLeditText.getText().toString().toLowerCase();
        String tmpPoortNr = poortEditText.getText().toString().toLowerCase();
        String tmpGebrNaam = gebruikersNaamET.getText().toString().toLowerCase();
        String tmpWw = wwET.getText().toString().toLowerCase();
        String tmpNieuweUrl;


        //als http:// er in zit:
        if (tmpCameraUrl.contains("http") || tmpCameraUrl.contains("http://")){
            try {
                tmpCameraUrl = tmpCameraUrl.replace("http://", "");
                tmpCameraUrl = tmpCameraUrl.replace("http", "");
            }
            catch (NullPointerException e){
                System.out.println("no more http in the url!");
            }

            Log.d("url zonder http=", "" + tmpCameraUrl);

        }

        //als er rtsp:// is ingevuld, weghalen
        if (tmpCameraUrl.startsWith("rtsp")){

            try {
                tmpCameraUrl = tmpCameraUrl.replace("rtsp://", "");
                tmpCameraUrl = tmpCameraUrl.replace("rtsp", "");
            }

            catch (NullPointerException e){
                System.out.println("no more rtsp in the url!");
            }

            Log.d("url zonder rtsp=", "" + tmpCameraUrl);
        }

        //If poort empty -> vul 554 in
        if (tmpPoortNr.isEmpty()){
            tmpPoortNr = "554";
        }

        //als er een wachtwoord + gebruikersnaam is ingevoerd
           if (!tmpGebrNaam.isEmpty() && !tmpWw.isEmpty()) {
               //voeg ze samen tot 1 url
               tmpNieuweUrl = "rtsp://" + tmpGebrNaam + ":" + tmpWw + "@" + tmpCameraUrl + ":" + tmpPoortNr;
           }

           else {
                //voeg ze samen tot 1 url
               tmpNieuweUrl = "rtsp://" + tmpCameraUrl + ":" + tmpPoortNr;
           }


        //set nieuwe url
        setCameraUrl(tmpNieuweUrl);

        Log.d("nieuwe url =", "" + tmpNieuweUrl);


        finish();
    }

    //Get current cameraurl function
    public static String getCurrentCameraUrl(){

        //if empty
        if (currentCameraUrl == null) {
            currentCameraUrl = defaultUrl;
        }

        return currentCameraUrl;
    }

    //Set camera url
    public static void setCameraUrl(String newUrl){

        //if empty
        if (newUrl == null) {
        }

        else{
            currentCameraUrl = newUrl;
        }
   }
}