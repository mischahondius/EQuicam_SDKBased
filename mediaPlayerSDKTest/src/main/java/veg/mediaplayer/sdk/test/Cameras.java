package veg.mediaplayer.sdk.test;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import EQuicamApp.R;

/**
 * Created by Equifilm on 16-6-2015.
 */
public class Cameras extends Activity {

    public static String currentCameraUrl;
    private static final String defaultUrl = "rtsp://live:6mxNfzAG@equicam.noip.me:554/?inst=1/?audio_mode=0/?enableaudio=1/?h26x=4";

    //Oncreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Camera's");

        //if empty
        if (currentCameraUrl == null) {
            currentCameraUrl = defaultUrl;
        }

        setContentView(R.layout.cameras);


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
    public static String setCameraUrl(String newUrl){
        currentCameraUrl = newUrl;
        return currentCameraUrl;
    }

}