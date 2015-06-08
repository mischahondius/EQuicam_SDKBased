/*
 *
 * Mischa Hondius, 6053017.
 * University of Amsterdam
 * SDK Used by Video Experts Group
 *
 */

package veg.mediaplayer.sdk.test;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import EQuicamApp.R;

public class Clips extends ListActivity{

  String videoDirectory;

  String[] videoFileList = {
          "/sdcard/Video/Android 2.0 Official Video_low.mp4",
          "/sdcard/Video/Android 2.2 Official Video_low.mp4",
          "/sdcard/Video/Android 2.3 Official Video_low.mp4",
          "/sdcard/Video/Android 3.0 Preview_low.mp4",
          "/sdcard/Video/Android Demo_low.mp4",
          "/sdcard/Video/Android in Spaaaaaace!.mp4",
          "/sdcard/Video/Android in Spaaaaaace!_low.mp4",
          "/sdcard/Video/What is an Android phone-_low.mp4"
  };

  public class MyThumbnaildapter extends ArrayAdapter<String>{

    public MyThumbnaildapter(Context context, int textViewResourceId,
                             String[] objects) {
      super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      View row = convertView;
      if(row==null){
        LayoutInflater inflater=getLayoutInflater();
        row=inflater.inflate(R.layout.row, parent, false);
      }

      TextView textfilePath = (TextView)row.findViewById(R.id.FilePath);
      textfilePath.setText(videoFileList[position]);
      ImageView imageThumbnail = (ImageView)row.findViewById(R.id.Thumbnail);

      Bitmap bmThumbnail;
      bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoFileList[position], Thumbnails.MICRO_KIND);
      imageThumbnail.setImageBitmap(bmThumbnail);

      return row;
    }

  }

//Oncreate
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

      //GET recordpth from intent
      Intent intent = getIntent();
      videoDirectory = intent.getStringExtra("Record Path");

      //Get files from directory
      Log.d("Files", "Path: " + videoDirectory);
      File f = new File(videoDirectory);
      File file[] = f.listFiles();
      Log.d("Files", "Size: "+ file.length);
      for (int i=0; i < file.length; i++)
      {
          Log.d("Files", "FileName:" + file[i].getName());
      }


      setListAdapter(new MyThumbnaildapter(Clips.this, R.layout.row, videoFileList));
  }
}