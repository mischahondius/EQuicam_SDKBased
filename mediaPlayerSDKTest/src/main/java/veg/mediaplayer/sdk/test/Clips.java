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
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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
import android.widget.TextView;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import EQuicamApp.R;

public class Clips extends ListActivity{

  public String videoDirectory;
  public ArrayList <String> videoArrayList;
  public ArrayList<String> sortedVideoArrayList;
  public String [] videoArray;
  public HashMap<String,Bitmap> cacheBitmap;
  public HashMap<String,String> durationCache;


  public class MyVideoListAdapter extends ArrayAdapter<String> {

      public MyVideoListAdapter(Context context, int textViewResourceId, ArrayList fileNames) {

          super(context, textViewResourceId, fileNames);

          //Initialize cachebitmap hashmap
          cacheBitmap = new HashMap<>(fileNames.size());
          initCacheBitmap(fileNames.size());

          //Initialize durationCache hashmap
          durationCache = new HashMap<>(fileNames.size());
          initDurationCache(fileNames.size());
      }


      //Thumbnail Cache aanmaken - Method
      public void initCacheBitmap(int size) {
          for (int i = 0; i < size; i++) {
              cacheBitmap.put(sortedVideoArrayList.get(i), ThumbnailUtils.createVideoThumbnail(videoDirectory + "/" + sortedVideoArrayList.get(i), Thumbnails.MINI_KIND));
          }
      }

      //Duration Cache aanmaken - Method
      public void initDurationCache(int size) {
          for (int i = 0; i < size; i++) {

            //Get metadata on duration
            durationCache.put(sortedVideoArrayList.get(i), getDuration(videoDirectory + "/" + sortedVideoArrayList.get(i)) );


          }
      }


//      //Thumbnail Cache aanmaken - Method
//      public void initCacheBitmap(int size) {
//          for (int i = 0; i < size; i++) {
//              cacheBitmap.put(sortedVideoArrayList.get(i), getVideoThumbnail(videoDirectory + "/" + sortedVideoArrayList.get(i)));
//          }
//      }


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

      //get duratie
      String tmpDuratie = durationCache.get(sortedVideoArrayList.get(position));



        if (tmpDuratie != null) {

            //omzetten naar minuten en seconden
            int tmpDuratieInt = Integer.parseInt(tmpDuratie);
            tmpDuratieInt = tmpDuratieInt/1000;

            long h = tmpDuratieInt / 3600;
            long m = (tmpDuratieInt - h * 3600) / 60;
            long s = tmpDuratieInt - (h * 3600 + m * 60);

            if (m == 1){
                tmpDuratie = "Lengte: " + m + " minuut " + s + " seconden";
            }

            else {
                tmpDuratie = "Lengte: " + m + " minuten " + s + " seconden";
            }


            //set duratie
            duratieTV.setText(tmpDuratie);

        }
        else{
            tmpDuratie = getString(R.string.lengteNietBeschikbaarStr);
        }


      //Get and set dATE AND TIME textview
      TextView textfilePath = (TextView)row.findViewById(R.id.dateTime);

      //GET FILENAME
        String tmpFileName = sortedVideoArrayList.get(position);

        //Gooi eind weg
        tmpFileName = tmpFileName.substring(0, Math.min(tmpFileName.length(), 14));

        //gooi underscores weg
        tmpFileName = tmpFileName.replace('_', ' ');

        //Voeg : toe aan tijd
        tmpFileName = tmpFileName.substring(0, 12) + ":" + tmpFileName.substring(12, tmpFileName.length());

        //Voeg "tijd :" toe
        tmpFileName = tmpFileName.substring(0, 9) + "\nTijd: " + tmpFileName.substring(9, tmpFileName.length());

        //voeg uur toe
        tmpFileName = tmpFileName.substring(0, 22) + " uur" + tmpFileName.substring(22, tmpFileName.length());

        //Voeg "Datum :" toe
        tmpFileName = tmpFileName.substring(0, 0) + "Datum: " + tmpFileName.substring(0, tmpFileName.length());

        //Voeg "/" toe
        tmpFileName = tmpFileName.substring(0, 12) + "/" + tmpFileName.substring(12, tmpFileName.length());
        tmpFileName = tmpFileName.substring(0, 15) + "/" + tmpFileName.substring(15, tmpFileName.length());

        //sla datum op in aparte string
        String datum = tmpFileName.substring(7, 18);



        //TODO datum naar woord?
        DateFormat format = new SimpleDateFormat(" yyyy/MM/dd", Locale.ENGLISH);
        Date date = new Date();
        try {
            date = format.parse(datum);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //datum back to string
        datum = date.toString();

        Log.d("Files", "datum: " + datum);


        //Gooi tijd weg
        datum = "" + datum.substring(0, 10) + ", " + datum.substring(datum.length()-4, datum.length());
        Log.d("Files", "datum zonder tijd: " + datum);

        //voeg datum in tmpfilename
        tmpFileName = tmpFileName.substring(0, 6) + " " + datum + tmpFileName.substring(18, tmpFileName.length());
        Log.d("Files", "alles bij elkaar: " + tmpFileName);


        textfilePath.setText(tmpFileName);

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
      setListAdapter(new MyVideoListAdapter(Clips.this, R.layout.videoitemfragment, sortedVideoArrayList));

  }

    public String getDuration(String path) {
        Log.d("Files", "GetDuration functie aangeroepen met path:" + path);

        MediaMetadataRetriever MetaDataOphaler = new MediaMetadataRetriever();
        String durationStr = new String();

        try {
            Log.d("Files", "Datasource path:" + path);

            MetaDataOphaler.setDataSource(path);
            durationStr = MetaDataOphaler.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            if (durationStr != null) {
                Log.d("Files", "Duration get succesvol, duration =" + durationStr);
            }

            if (durationStr == null) {
                Log.d("Files", "Duration get NIET succesvol, duration =" + durationStr);
            }
        } catch (Exception e) {
            durationStr = "0";
            Log.d("Files", "Exception e");

        } finally {
            MetaDataOphaler.release();
        }
        return durationStr;
    }
}