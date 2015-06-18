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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import EQuicamApp.R;

public class ClipsActivity extends ListActivity {

  public String videoDirectory;
  public ArrayList <String> videoArrayList;
  public ArrayList <String> sortedVideoArrayList;
  public String [] videoArray;
  public HashMap<String, Clip> clipCache;

  public class MyVideoListAdapter extends ArrayAdapter<String> {

      public MyVideoListAdapter(Context context, int textViewResourceId, ArrayList fileNames) {

          super(context, textViewResourceId, fileNames);

          //Initialize cachebitmap hashmap
          if (clipCache == null){
              clipCache = new HashMap<>(fileNames.size());
              Log.d("Files", "Clip cache was nog niet instantieerd, dus nieuwe aangemaakt");

          }

          //Maak cache aan voor clips
          initClipCache(fileNames.size());
      }


      //Method to make a cache containing clips
      public void initClipCache(int size) {
          for (int i = 0; i < size; i++) {

              Log.d("Files", "we zitten in de initcachebitmap functie");

              //nieuwe clip aanmaken
              Clip tmpClip = new Clip(sortedVideoArrayList.get(i));

              //Sla op in cache
              clipCache.put(sortedVideoArrayList.get(i), tmpClip);

          }
      }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
      if(row==null){
        LayoutInflater inflater = getLayoutInflater();
        row = inflater.inflate(R.layout.videoitemfragment, parent, false);
      }

      //TODO datum in Nederlands en dagnummer op andere plek
      //Get duratie Textvie
      TextView duratieTV = (TextView)row.findViewById(R.id.duratieTV);


        //get Afspeelduur
        String afspeelDuur = clipCache.get(sortedVideoArrayList.get(position)).getAfspeelDuur();

        if (afspeelDuur != null) {

            //omzetten naar minuten en seconden
            int tmpDuratieInt = Integer.parseInt(afspeelDuur);
            tmpDuratieInt = tmpDuratieInt/1000;

            long h = tmpDuratieInt / 3600;
            long m = (tmpDuratieInt - h * 3600) / 60;
            long s = tmpDuratieInt - (h * 3600 + m * 60);

            if (m == 1){
                afspeelDuur = "Lengte: " + m + " minuut " + s + " seconden";
            }

            else {
                afspeelDuur = "Lengte: " + m + " minuten " + s + " seconden";
            }


            //set duratie
            duratieTV.setText(afspeelDuur);

        }
        else{
            afspeelDuur = getString(R.string.lengteNietBeschikbaarStr);
        }


      //Get and set dATE AND TIME textview
      TextView textfilePath = (TextView)row.findViewById(R.id.dateTime);

      //GET FILENAME
        String tmpFileName = sortedVideoArrayList.get(position);

//        //Gooi eind weg
//        tmpFileName = tmpFileName.substring(0, Math.min(tmpFileName.length(), 14));
//
//        //gooi underscores weg
//        tmpFileName = tmpFileName.replace('_', ' ');
//
//        //Voeg : toe aan tijd
//        tmpFileName = tmpFileName.substring(0, 12) + ":" + tmpFileName.substring(12, tmpFileName.length());
//
//        //Voeg "tijd :" toe
//        tmpFileName = tmpFileName.substring(0, 9) + "\nTijd: " + tmpFileName.substring(9, tmpFileName.length());
//
//        //voeg uur toe
//        tmpFileName = tmpFileName.substring(0, 22) + " uur" + tmpFileName.substring(22, tmpFileName.length());
//
//        //Voeg "Datum :" toe
//        tmpFileName = tmpFileName.substring(0, 0) + "Datum: " + tmpFileName.substring(0, tmpFileName.length());
//
//        //Voeg "/" toe
//        tmpFileName = tmpFileName.substring(0, 12) + "/" + tmpFileName.substring(12, tmpFileName.length());
//        tmpFileName = tmpFileName.substring(0, 15) + "/" + tmpFileName.substring(15, tmpFileName.length());
//
//        //sla datum op in aparte string
//        String datum = tmpFileName.substring(7, 18);
//
//
//
//        //TODO datum naar woord?
//        DateFormat format = new SimpleDateFormat(" yyyy/MM/dd", Locale.ENGLISH);
//        Date date = new Date();
//        try {
//            date = format.parse(datum);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        //datum back to string
//        datum = date.toString();
//
//        Log.d("Files", "datum: " + datum);
//
//
//        //Gooi tijd weg
//        datum = "" + datum.substring(0, 10) + ", " + datum.substring(datum.length()-4, datum.length());
//        Log.d("Files", "datum zonder tijd: " + datum);
//
//        //voeg datum in tmpfilename
//        tmpFileName = tmpFileName.substring(0, 6) + " " + datum + tmpFileName.substring(18, tmpFileName.length());
//        Log.d("Files", "alles bij elkaar: " + tmpFileName);


        textfilePath.setText(tmpFileName);

      //Create and set thumbnails
      ImageView imageThumbnail = (ImageView)row.findViewById(R.id.Thumbnail);

      //check of niet leeg, als leeg, equifilm thumb
      // Anders duimnagel ophalen van clip

      if (clipCache.get(sortedVideoArrayList.get(position)) != null)
      {
        imageThumbnail.setImageBitmap(clipCache.get(sortedVideoArrayList.get(position)).getDuimNagel());
      }

        //Geen idee waarom, maar dit was nodig
        //Wat een gekloot zeg
        final int tmpPosition = position;

        //setonClickListener voor rij
        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Set fulscreen View
                Intent i = new Intent(getApplicationContext(), FullScreenVideoActivity.class);

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

    videoArrayList = new ArrayList<>();

      //GET recordpath from intent
      Intent intent = getIntent();
      videoDirectory = intent.getStringExtra("Record Path");

      //Get files from directory
      File f = new File(videoDirectory);
      File file[] = f.listFiles();
      Log.d("Files", "Size: "+ file.length);

    //iterate over files heen
      for (File aFile : file) {
          Log.d("Files", "FileName:" + aFile.getName());
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

  }


}