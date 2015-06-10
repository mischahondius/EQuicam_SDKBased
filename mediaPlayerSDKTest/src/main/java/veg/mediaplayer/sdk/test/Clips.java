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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import EQuicamApp.R;

public class Clips extends ListActivity{

  public String videoDirectory;
  public ArrayList <String> videoArrayList;
  public ArrayList<String> sortedVideoArrayList;
  public String [] videoArray;
  public HashMap<String,Bitmap> cacheBitmap;


  public class MyVideoListAdapter extends ArrayAdapter<String> {

      public MyVideoListAdapter(Context context, int textViewResourceId, ArrayList fileNames) {

          super(context, textViewResourceId, fileNames);

          //Initialize cachebitmap hashmap
          cacheBitmap = new HashMap<String, Bitmap>(fileNames.size());

          initCacheBitmap(fileNames.size());
      }


      //Thumbnail Cache aanmaken - Method
      public void initCacheBitmap(int size) {
          for (int i = 0; i < size; i++) {
              cacheBitmap.put(sortedVideoArrayList.get(i), ThumbnailUtils.createVideoThumbnail(videoDirectory + "/" + sortedVideoArrayList.get(i), Thumbnails.MINI_KIND));
          }
      }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
      if(row==null){
        LayoutInflater inflater = getLayoutInflater();
        row = inflater.inflate(R.layout.videoitemfragment, parent, false);
      }

      //Get and set filename textview
      TextView textfilePath = (TextView)row.findViewById(R.id.FilePath);
      textfilePath.setText(sortedVideoArrayList.get(position));

      //Create and set thumbnails
      ImageView imageThumbnail = (ImageView)row.findViewById(R.id.Thumbnail);

      //check of niet leeg, als leeg, equifilm thumb
      if (cacheBitmap.get(sortedVideoArrayList.get(position)) != null)
      {
        imageThumbnail.setImageBitmap(cacheBitmap.get(sortedVideoArrayList.get(position)));
      }

        //Geen idee waarom, maar dit was nodig
        //Wat een gekloot zeg
        final int tmpPosition = position;

        //setonClickListener voor rij
        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Set fulscreen View
                Intent i = new Intent(getApplicationContext(), FullScreenVideoPlayer.class);

                //Put recordpath
                i.putExtra("VideoLocation", videoDirectory + "/" + sortedVideoArrayList.get(tmpPosition));

                startActivity(i);
            }
        });

      return row;
    }
  }

//Oncreate
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

    videoArrayList = new ArrayList<String>();

      //GET recordpath from intent
      Intent intent = getIntent();
      videoDirectory = intent.getStringExtra("Record Path");

      //Get files from directory
      File f = new File(videoDirectory);
      File file[] = f.listFiles();
      Log.d("Files", "Size: "+ file.length);

    //iterate over files heen
      for (int i=0; i < file.length; i++)
      {
          Log.d("Files", "FileName:" + file[i].getName());
          videoArrayList.add(file[i].getName());
      }

      //ArrayLijst omzetten naar Array
      videoArray = videoArrayList.toArray(new String[videoArrayList.size()]);

      //Array omkeren (nieuwste video's bovenaan)
      Arrays.sort(videoArray, Collections.reverseOrder());

      //Array back to arraylist
      sortedVideoArrayList = new ArrayList<String>(Arrays.asList(videoArray));

      //SetListAdapter
      setListAdapter(new MyVideoListAdapter(Clips.this, R.layout.videoitemfragment, sortedVideoArrayList));

  }
}