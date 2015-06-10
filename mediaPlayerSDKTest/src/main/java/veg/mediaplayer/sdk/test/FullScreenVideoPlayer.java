package veg.mediaplayer.sdk.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.HashMap;

import EQuicamApp.R;

/**
 * Created by Equifilm on 10-6-2015.
 */

public class FullScreenVideoPlayer extends Activity implements MediaPlayer.OnCompletionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET VideoLocation from intent
        Intent intent = getIntent();
        String videoLocation = intent.getStringExtra("VideoLocation");

        setContentView(R.layout.fullscreen_video_player);

        //Get videoview
        VideoView fullScreenVideoView = (VideoView) findViewById(R.id.videoView);

        if (videoLocation != null) {
            fullScreenVideoView.setMediaController(new MediaController(this));
            fullScreenVideoView.setOnCompletionListener(this);
            fullScreenVideoView.setVideoURI(Uri.parse(videoLocation));
            fullScreenVideoView.start();
        }


        if (videoLocation == null) {
            throw new IllegalArgumentException("Video is beschadigd.");
        }
}

    @Override
    public void onCompletion(MediaPlayer v) {
        finish();
    }

}