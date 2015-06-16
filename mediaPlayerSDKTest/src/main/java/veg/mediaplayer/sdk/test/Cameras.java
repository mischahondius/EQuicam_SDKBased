package veg.mediaplayer.sdk.test;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import EQuicamApp.R;

/**
 * Created by Equifilm on 16-6-2015.
 */
public class Cameras extends Activity {

    private static final String         defaultUrl = "rtsp://live:6mxNfzAG@equicam.noip.me:554/?inst=1/?audio_mode=0/?enableaudio=1/?h26x=4";
    public static String                currentCameraUrl = defaultUrl;
    private EditText                    cameraURLeditText;
    private Button                      camerasSubmitBtn;
    private EditText                    poortEditText;
    private EditText                    gebruikersNaamET;
    private EditText                    wwET;

    //Oncreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Camera's");
        setContentView(R.layout.cameras);

        //setkeyboard to appear directly
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //if empty
        if (currentCameraUrl.isEmpty()) {
            currentCameraUrl = defaultUrl;
        }

        //get buttons
        cameraURLeditText = (EditText) findViewById(R.id.cameraURLeditText);
        camerasSubmitBtn = (Button) findViewById(R.id.camerasSubmitBtn);
        poortEditText = (EditText) findViewById(R.id.poortEditText);
        gebruikersNaamET = (EditText) findViewById(R.id.gebruikersNaamET);
        wwET = (EditText) findViewById(R.id.wwET);
        camerasSubmitBtn = (Button) findViewById(R.id.camerasSubmitBtn);

    }

    //onclicklistener submit
    public void onSubmit(View view){

        //alle info opslaan
        String tmpCameraUrl = cameraURLeditText.getText().toString();
        String tmpPoortNr = poortEditText.getText().toString();
        String tmpGebrNaam = gebruikersNaamET.getText().toString();
        String tmpWw = wwET.getText().toString();
        String tmpNieuweUrl;


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
        if (currentCameraUrl.isEmpty()) {
            currentCameraUrl = defaultUrl;
        }
        return currentCameraUrl;
    }


    //Set camera url
    public static void setCameraUrl(String newUrl){
        currentCameraUrl = newUrl;
    }

}