package veg.mediaplayer.sdk.test;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
        String tmpCameraUrl = new String();
        tmpCameraUrl = cameraURLeditText.getText().toString();
        Toast.makeText(this, "Ingevoerde url: " + tmpCameraUrl, Toast.LENGTH_LONG).show();

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
    public static String setCameraUrl(String newUrl){
        currentCameraUrl = newUrl;
        return currentCameraUrl;
    }

}