/*
 *
 * Mischa Hondius, 6053017.
 * University of Amsterdam
 * SDK Used by Video Experts Group
 *
 */

package veg.mediaplayer.sdk.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Clip {

    //Een clip bezit de volgende eigenschappen
    private String              bestandsNaam;
    private String              bestandsMap;
    private String              bestandsLocatie;

    private String              duimNagelMap;
    private String              duimNagelLocatie;

    private String              datum;
    private String              tijd;
    private String              afspeelDuur;

    //tag voor logs
    private static final String 		TAG = "EQuicamAPP";

    //Clip constructor
    public Clip(String bestandsNaam){
        this.bestandsNaam = bestandsNaam;
        this.bestandsMap = MainActivity.getOpnameMap();
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
        duimNagelNaarMap();
    }

    public Bitmap getDuimNagel(){
        return BitmapFactory.decodeFile(this.duimNagelLocatie);
    }

    public void setDatum() {
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
    }

    public String getDatum(){
        return this.datum;
    }

    public void setTijd () {
        String tmpTijd = this.bestandsNaam;
        tmpTijd = tmpTijd.substring(10, 12) + ":" + tmpTijd.substring(12, 14) + " uur";
        this.tijd = tmpTijd;
    }

    public String getTijd () {
        return this.tijd;
    }

    public String getBestandsLocatie(){
        return this.bestandsLocatie;
    }

    //DuimNagel wegschrijven naar map
    public void duimNagelNaarMap()
    {

        //Als Duimnagellocatie nog leeg is, vul hem dan pas in!
        if (duimNagelLocatie == null) {
            //Mapje maken voor thumbs
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "EQuicam Thumbnails");

            if (! mediaStorageDir.exists()){
                if (!(mediaStorageDir.mkdirs() || mediaStorageDir.isDirectory())){
                    Log.e(TAG, "Niet gelukt op opnamepad voor Thumbnails aan te maken");
                }
            }
            this.duimNagelMap = mediaStorageDir.getPath();

            Bitmap tmpThumbNail = ThumbnailUtils.createVideoThumbnail(this.bestandsLocatie, MediaStore.Video.Thumbnails.MINI_KIND);

            //replace .mp4 with .jpg
            if (this.bestandsLocatie.endsWith(".mp4")) {
                this.duimNagelLocatie =  this.duimNagelMap + "/" + this.bestandsNaam.substring(0, this.bestandsNaam.length() - 4) + ".jpg";
            }

            try {
                FileOutputStream out = new FileOutputStream(this.duimNagelLocatie);
                tmpThumbNail.compress(Bitmap.CompressFormat.JPEG, 30, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                //todo
            }
        }
    }
}