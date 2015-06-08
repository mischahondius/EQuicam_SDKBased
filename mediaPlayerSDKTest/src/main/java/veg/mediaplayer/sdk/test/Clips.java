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
import android.renderscript.Element;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import EQuicamApp.R;

public class Clips extends ListActivity{

  public String videoDirectory;
  public ArrayList <String> videoArrayList;
    public ArrayList<String> sortedVideoArrayList;
    public String [] videoArray;


  public class MyThumbnaildapter extends ArrayAdapter<String>{

    public MyThumbnaildapter(Context context, int textViewResourceId,
                             ArrayList objects) {
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
      textfilePath.setText(sortedVideoArrayList.get(position));

      //Create and set thumbnails
      ImageView imageThumbnail = (ImageView)row.findViewById(R.id.Thumbnail);
      Bitmap bmThumbnail;
      bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoDirectory + "/" + sortedVideoArrayList.get(position), Thumbnails.MINI_KIND);

      //check of niet leeg, als leeg, equifilm thumb
      if (bmThumbnail != null)
      {
        imageThumbnail.setImageBitmap(bmThumbnail);
      }

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
      setListAdapter(new MyThumbnaildapter(Clips.this, R.layout.row, sortedVideoArrayList));

  }
}