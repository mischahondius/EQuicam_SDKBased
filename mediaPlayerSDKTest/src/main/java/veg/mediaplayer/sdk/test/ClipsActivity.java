/*
 *
 * Mischa Hondius, 6053017.
 * University of Amsterdam
 * SDK Used by Video Experts Group
 *
 */

package veg.mediaplayer.sdk.test;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RawRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import android.os.Handler;
import EQuicamApp.R;

public class ClipsActivity extends ListActivity {

    //Todo: kan alles private worden?
  public String                         videoDirectory;
  public ArrayList <String>             videoArrayList;
  public ArrayList <String>             sortedVideoArrayList;
  public String []                      videoArray;
  public HashMap<String, Clip>          clipCache;

  public class MyVideoListAdapter extends ArrayAdapter<String> {

      public MyVideoListAdapter(Context context, int textViewResourceId, ArrayList<String> fileNames) {
          super(context, textViewResourceId, fileNames);

          clipCache = new HashMap<>(fileNames.size());

          //Maak cache aan voor clips
          initClipCache(fileNames.size());
      }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
      if(row==null){
        LayoutInflater inflater = getLayoutInflater();
        row = inflater.inflate(R.layout.videoitemfragment, parent, false);
      }

      //Set Afspeelduur
      TextView duratieTV = (TextView)row.findViewById(R.id.duratieTV);
      String afspeelDuur = clipCache.get(sortedVideoArrayList.get(position)).getAfspeelDuur();
      duratieTV.setText(afspeelDuur);

      //Set Datum
      TextView datumTV = (TextView)row.findViewById(R.id.datumTV);
      datumTV.setText(clipCache.get(sortedVideoArrayList.get(position)).getDatum());

      //Set Tijd
      TextView tijdTV = (TextView)row.findViewById(R.id.tijdTV);
      tijdTV.setText(clipCache.get(sortedVideoArrayList.get(position)).getTijd());

      //Set Duimnagel
      ImageView imageThumbnail = (ImageView)row.findViewById(R.id.Thumbnail);

      // Anders duimnagel ophalen van clip
      if (clipCache.get(sortedVideoArrayList.get(position)) != null)
      {
        imageThumbnail.setImageBitmap(clipCache.get(sortedVideoArrayList.get(position)).getDuimNagel());
      }

        //Dit was even nodig
        final int tmpPosition = position;

        //setonClickListener voor rij
        row.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            //Open fulscreen View voor video
            Intent i = new Intent(getApplicationContext(), FullScreenVideoActivity.class);

            //Geeg bestandslocatie mee aan FullScreen Player
            i.putExtra("VideoLocation", clipCache.get(sortedVideoArrayList.get(tmpPosition)).getBestandsLocatie());

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

      //Maak arraylist om te vullen met video's
      videoArrayList = new ArrayList<>();

      //GET recordpath from intent
      Intent intent = getIntent();
      videoDirectory = intent.getStringExtra("Record Path");

      //Get files from directory
      File f = new File(videoDirectory);
      File file[] = f.listFiles();

      //iterate over files heen
      for (File aFile : file) {
          videoArrayList.add(aFile.getName());
      }

      //ArrayLijst omzetten naar Array
      videoArray = videoArrayList.toArray(new String[videoArrayList.size()]);

      //Array omkeren (nieuwste video's bovenaan)
      Arrays.sort(videoArray, Collections.reverseOrder());

      //Array back to arraylist
      sortedVideoArrayList = new ArrayList<>(Arrays.asList(videoArray));

      //SetListAdapter
      setListAdapter(new MyVideoListAdapter(ClipsActivity.this, R.layout.videoitemfragment, sortedVideoArrayList));
//
      //Als lijst leeg is
      if (sortedVideoArrayList.isEmpty()) {
          Toast.makeText(this, getString(R.string.geenClipsOmWeerTeGevenStr), Toast.LENGTH_LONG).show();
      }
  }

    //Functie voor het opzetten van een ClipCache
    public void initClipCache(int size) {
        for (int i = 0; i < size; i++) {

            //nieuwe clip aanmaken
            Clip tmpClip = new Clip(sortedVideoArrayList.get(i));

            //Sla Clip op in cache
            clipCache.put(sortedVideoArrayList.get(i), tmpClip);
        }

    }
}