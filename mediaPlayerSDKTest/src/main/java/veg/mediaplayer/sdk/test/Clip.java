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

import EQuicamApp.R;

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
        this.bestandsLocatie = this.bestandsMap + "/" + this.bestandsNaam;
        this.getMetaData();
        this.setDuimNagel();
        this.setDatum();
        this.setTijd();
    }

    public void getMetaData() {

        MediaMetadataRetriever MetaDataOphaler = new MediaMetadataRetriever();

        try {
            MetaDataOphaler.setDataSource(this.bestandsLocatie);
            this.afspeelDuur = MetaDataOphaler.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            this.datum = MetaDataOphaler.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);

            if (this.afspeelDuur != null) {}

            if (this.afspeelDuur == null) {
                this.afspeelDuur = "0";
            }

        } catch (Exception e) {
            this.afspeelDuur = "0";
            Log.d("Files", "Exception e");

        } finally {
            MetaDataOphaler.release();
        }

        if (this.afspeelDuur != null) {

            //omzetten naar minuten en seconden
            int tmpDuratieInt = Integer.parseInt(afspeelDuur);
            tmpDuratieInt = tmpDuratieInt/1000;

            long h = tmpDuratieInt / 3600;
            long m = (tmpDuratieInt - h * 3600) / 60;
            long s = tmpDuratieInt - (h * 3600 + m * 60);

            if (m == 1){
                this.afspeelDuur = m + " minuut " + s + " seconden";
            }

            else {
                this.afspeelDuur = m + " minuten " + s + " seconden";
            }
        }
        else{
            this.afspeelDuur = "Niet beschikbaar";
        }
    }

    public String getAfspeelDuur(){
        return this.afspeelDuur;
    }

    public void setDuimNagel(){
        this.duimNagel = ThumbnailUtils.createVideoThumbnail(this.bestandsLocatie, MediaStore.Video.Thumbnails.MINI_KIND);
    }

    public Bitmap getDuimNagel(){
        return this.duimNagel;
    }

    public void setDatum() {
        this.datum = "10 april 2015";
    }

    public String getDatum(){

//        tmp String aanmaken
          String tmpDatum = this.bestandsNaam;

//        Gooi underscores weg
          tmpDatum = tmpDatum.replace("_", "");

        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.GERMAN);
        Date date = new Date();
        try {
            date = format.parse(tmpDatum);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //datum back to string
        tmpDatum = date.toString();

        //DAG                                       DATUM                             MAAND                            JAAR
        tmpDatum = tmpDatum.substring(0, 3) + " " + tmpDatum.substring(8, 10) + " " + tmpDatum.substring(4, 7) + " " + tmpDatum.substring(25, 29);

        tmpDatum = tmpDatum.replace("Mon", "Maandag");
        tmpDatum = tmpDatum.replace("Tue", "Dinsdag");
        tmpDatum = tmpDatum.replace("Wed", "Woensdag");
        tmpDatum = tmpDatum.replace("Thu", "Donderdag");
        tmpDatum = tmpDatum.replace("Fri", "Vrijdag");
        tmpDatum = tmpDatum.replace("Sat", "Zaterdag");
        tmpDatum = tmpDatum.replace("Sun", "Zondag");

        tmpDatum = tmpDatum.replace("Jan", "januari");
        tmpDatum = tmpDatum.replace("Feb", "februari");
        tmpDatum = tmpDatum.replace("Mar", "maart");
        tmpDatum = tmpDatum.replace("Apr", "april");
        tmpDatum = tmpDatum.replace("May", "mei");
        tmpDatum = tmpDatum.replace("Jun", "juni");
        tmpDatum = tmpDatum.replace("Jul", "juli");
        tmpDatum = tmpDatum.replace("Aug", "augustus");
        tmpDatum = tmpDatum.replace("Sep", "september");
        tmpDatum = tmpDatum.replace("Oct", "oktober");
        tmpDatum = tmpDatum.replace("Nov", "november");
        tmpDatum = tmpDatum.replace("Dec", "december");

        this.datum = tmpDatum;
        return this.datum;
    }

    public void setTijd () {
        this.tijd = "00:00";
        //todo
    }

    public String getTijd () {
        //todo
        return this.tijd;
    }

    public String getBestandsLocatie(){
        return this.bestandsLocatie;
    }
}
