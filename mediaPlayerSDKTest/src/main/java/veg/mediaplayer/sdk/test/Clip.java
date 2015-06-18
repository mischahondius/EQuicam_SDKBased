package veg.mediaplayer.sdk.test;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Equifilm on 18-6-2015.
 */
public class Clip {

    //Een clip bezit de volgende eigenschappen
    private String              bestandsNaam;
    private String              bestandsMap;
    private String              bestandsLocatie;
    private String              datum;
    private String              tijd;
    private String              afspeelDuur;
    private Bitmap              duimNagel;

    //Clip constructor
    public Clip(String bestandsNaam){
        this.bestandsNaam = bestandsNaam;
        this.bestandsMap = MainActivity.getRecordPath();
        this.bestandsLocatie = bestandsMap + "/" + this.bestandsNaam;
        this.verkijgAfpeelDuurVanMetadata();
        this.duimNagelAanmaken();
        this.setDatum();
        this.setTijd();
    }

    public void verkijgAfpeelDuurVanMetadata() {

        Log.d("Files", "GetDuration functie aangeroepen met path:" + this.bestandsLocatie);
        MediaMetadataRetriever MetaDataOphaler = new MediaMetadataRetriever();

        try {
            Log.d("Files", "Datasource path:" + this.bestandsLocatie);

            MetaDataOphaler.setDataSource(this.bestandsLocatie);
            this.afspeelDuur = MetaDataOphaler.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            if (this.afspeelDuur != null) {
                Log.d("Files", "Duration get succesvol, duration =" + this.afspeelDuur);
            }

            if (this.afspeelDuur == null) {
                Log.d("Files", "Duration get NIET succesvol, duration =" + this.afspeelDuur);
            }
        } catch (Exception e) {
            this.afspeelDuur = "0";
            Log.d("Files", "Exception e");

        } finally {
            MetaDataOphaler.release();
        }
    }

    public String getAfspeelDuur(){
        return this.afspeelDuur;
    }

    public void duimNagelAanmaken (){
        this.duimNagel = ThumbnailUtils.createVideoThumbnail(this.bestandsLocatie, MediaStore.Video.Thumbnails.MINI_KIND);
    }

    public Bitmap getDuimNagel(){
        return this.duimNagel;
    }

    public void setDatum() {
        this.datum = "10 april 2015";
    }


//        //todo
//        //tmp String aanmaken
////        String tmpFileName = this.bestandsNaam;
//
////        //Gooi eind weg
////        tmpFileName = tmpFileName.substring(0, Math.min(tmpFileName.length(), 14));
////
////        //gooi underscores weg
////        tmpFileName = tmpFileName.replace('_', ' ');
////
////        //Voeg "/" toe
////        tmpFileName = tmpFileName.substring(0, 12) + "/" + tmpFileName.substring(12, tmpFileName.length());
////        tmpFileName = tmpFileName.substring(0, 15) + "/" + tmpFileName.substring(15, tmpFileName.length());
//
//        //sla datum op in aparte string
////        this.datum = tmpFileName.substring(7, 18);
////
////        //TODO datum naar woord?
////        DateFormat format = new SimpleDateFormat(" yyyy/MM/dd", Locale.ENGLISH);
////        Date date = new Date();
////        try {
////            date = format.parse(datum);
////        } catch (ParseException e) {
////            e.printStackTrace();
////        }
////
//////        //datum back to string
//////        datum = date.toString();
////
////        Log.d("Files", "datum: " + datum);
////
////        //Gooi tijd weg
////        this.datum = "" + datum.substring(0, 10) + ", " + datum.substring(datum.length() - 4, datum.length());
////        Log.d("Files", "datum zonder tijd: " + datum);
////
//////        //voeg datum in tmpfilename
//////        tmpFileName = tmpFileName.substring(0, 6) + " " + datum + tmpFileName.substring(18, tmpFileName.length());
//////        Log.d("Files", "alles bij elkaar: " + tmpFileName);
//
//    }

    public void setTijd () {
        this.tijd = "00:00";
        //todo
    }

}
