/*
 *
 * Mischa Hondius, 6053017.
 * University of Amsterdam
 * SDK Used by Video Experts Group
 *
 */

package veg.mediaplayer.sdk.test;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import EQuicamApp.R;

public class FullScreenVideoActivity extends Activity implements MediaPlayer.OnCompletionListener {

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
            fullScreenVideoView.setMediaController(new MediaController(this, false));
            fullScreenVideoView.setOnCompletionListener(this);
            fullScreenVideoView.setVideoURI(Uri.parse(videoLocation));
            fullScreenVideoView.start();
        }

        if (videoLocation == null) {
            throw new IllegalArgumentException(getString(R.string.videoNietAanwezigStr));
        }
}

    @Override
    public void onCompletion(MediaPlayer v) {
        finish();
    }

}