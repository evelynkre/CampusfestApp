package com.example.campusfestapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

/**
 * In der Klasse Artist werden Infos, Videos und das Logo der artist dargstellt.
 * Sollte etwas nicht vorhanden sein, wird dieses einfach nicht angezeigt.
 */
public class Artist extends AppCompatActivity {
    private Storage storage;
    private JSONObject artistData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Falls die Aktivitaet vorher bereits geoffnet war, kann man dieser einen Status mitgeben.
        //(Bspws. ScrollPosition oder aehnliches)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        storage = new Storage(this);
        //Bekommt den Kuenstlernamen von der vorherigen Aktivitaet (LineUp oder Timetable)
        String artistName = getIntent().getStringExtra("artistName");
        //Anhand des Kuenstlernames werden alle Informationen zu der artist aus dem Internal Storage
        //geholt
        artistData = storage.getJsonFromList("Artists", "name", artistName);
        if (artistData == null) {
            //Wenn kein Kuenstler vorhanden ist, wird eine Warnung auf dem Bildschirm ausgegeben,
            //dass keine Internetverbindung besteht.
            AlertSingleton.getInstance(this).showAlert(R.string.error_title, R.string.error_no_internet);
            finish();
        }
        //Wenn der Kuenstler eine logoUri hat wird versucht dieses zu laden.
        if (artistData.has("logoUri")) {
            setLogo(artistName);
        } else {
            setArtistName(artistName);
        }
        //Wenn der Kuenstler ein Bild hat wird versucht dieses zu laden.
        if (artistData.has("imageUri") || artistData.has("videoUri")) {
            setImageAndPlayButton();
        }
        //Wenn der Kuenstler eine Beschreibung hat wird versucht diese zu laden.
        if (artistData.has("description")) {
            setArtistDescription();
        }
    }

    /**
     * Setzt den Namen auf die TextView des Kuenstlers(wenn dieser kein Logo hat) und zeigt
     * die TextView an.
     * @param artistName Name des Kuenstlers
     */
    protected void setArtistName(String artistName){
        TextView artistNameText = findViewById(R.id.artistName);
        artistNameText.setText(artistName);
        artistNameText.setVisibility(View.VISIBLE);
    }

    /**
     * Setzt das Logo des Kuenstlers, wenn das Logo nicht gesetzt werden kann, wird der
     * Name des Kuenstlers in ein TextView eingetragen.
     * @param artistName
     */
    private void setLogo(String artistName){
        try {
            ImageView logoView = findViewById(R.id.artistLogo);
            //Die LogoUrl wird aus den Daten aus dem Internal Storage geholt.
            String logoUrl = artistData.getString("logoUri");
            //Zum dynamischen Laden des Bildes von der Url wurde eine eigene Klasse geschrieben.
            Bitmap logoImage = new DownloadImage(logoView).execute(logoUrl).get();
            //Wenn das Bild erfolgreich in die ImageView geladen wurde, wird diese sichtbar gemacht.
            if (logoImage != null) {
                logoView.setVisibility(View.VISIBLE);
            } else {
                //Wenn kein Bild geladen werden konnte wird der Kuenstlername als Text eingefuegt.
                setArtistName(artistName);
            }
            //Sollte irgendeine Exception beim Logo laden auftreten, wird der Name des Kuenstlers
            //in einer TextView dargestellt.
        } catch (JSONException e) {
            setArtistName(artistName);
        } catch (InterruptedException e) {
            setArtistName(artistName);
        } catch (ExecutionException e) {
            setArtistName(artistName);
        }
    }

    /**
     * Fuegt ein Bild des Kuenstlers hinzu.
     * Wenn ein Bild und ein Video vorhanden sind, wird auch ein Playbutton angezeigt.
     * Wenn auf den Playbutton gedrueckt wird, wird die Video Aktivitaet gestartet und das
     * Video des Kuenstlers abgespielt.
     */
    private void setImageAndPlayButton(){
        try {
            //Die BildUrl wird aus den Daten aus dem Internal Storage geholt.
            String imageUri = artistData.getString("imageUri");
            ImageView playButton = findViewById(R.id.playButton);
            ImageView artistImage = findViewById(R.id.artistImage);
            try {

                //Zum dynamischen Laden des Bildes von der Url wurde eine eigene Klasse geschrieben.
                Bitmap loadImage = new DownloadImage(artistImage).execute(imageUri).get();
                //Wenn das Bild erfolgreich in die ImageView geladen wurde, wird dieses sichtbar gemacht.
                if (loadImage != null) {
                    artistImage.setImageBitmap(loadImage);
                    artistImage.setVisibility(View.VISIBLE);
                }else {
                    //Zum dynamischen Laden des Bildes von der Url wurde eine eigene Klasse geschrieben.
                    artistImage.setBackground(ContextCompat.getDrawable(this, R.drawable.background_no_video));
                    artistImage.setVisibility(View.VISIBLE);
                }
                //Wenn der Kuenstler ein Video hat, wird versucht dieses zu laden.
                if (artistData.has("videoUri")) {
                    //Die VideoUrl wird aus der in StartUp definierten Server addresse und der "videoUri"
                    //aus den Daten aus dem Internal Storage zusammengebaut.
                    String videoUrl = StartUp.SERVER_ADDRESS + artistData.getString("videoUri");
                    //Es wird geschaut ob der Mimetype passt, dieser muss ein MP4 sein.
                    String mimeType = URLConnection.guessContentTypeFromName(videoUrl);
                    if (mimeType != null && mimeType.startsWith("video/mp4")) {
                        //Wenn der Mimetype passt, wird der Play Button sichtbar gemacht.
                        //Der Play button ist ein transparentes PNG, das auf das Bild gesetzt wird.
                        playButton.setVisibility(View.VISIBLE);
                    }
                }
            } catch (ExecutionException e) {}
            catch (InterruptedException e) {}
        } catch (JSONException e) {}
    }

    /**
     * Setzt die Beschreibung des Kuenstlers in die dafuer vorgesehene TextView und
     * zeigt die Textview an
     */
    private void setArtistDescription(){
        try {
            //Die Beschreibung wird aus den zuvor aus dem Internal Storage geholten Daten
            //geladen.
            String description = artistData.getString("description");
            TextView artistDescription = findViewById(R.id.artistDescription);
            //In die TextView wird die Beschreibung des Kuenstlers eingefuegt.
            artistDescription.setText(description);
            //Die TextView, die die Beschreibung enthaelt wird sichtbar gemacht.
            artistDescription.setVisibility(View.VISIBLE);
        } catch (JSONException e) {}
    }

    /**
     * Wenn der PlayButton auf dem Video gedrueckt wird, wird die Video Aktivitaet aufgerufen.
     * Dieser wird die Uri fuer das Video uebergeben. (bspws: "/videos/Bastille.mp4")
     * @param v
     */
    protected void playVideo(View v){
        //Erzeugt ein neues Intent der Klasse Video.
        Intent Video = new Intent(this, Video.class);
        try {
            //Fuegt dem Intent die Information der VideoUri hinzu (bspws: "/videos/Bastille.mp4")
            Video.putExtra("videoUri", artistData.getString("videoUri"));
            //Startet die Video Aktivitaet.
            startActivity(Video);
        } catch (JSONException e) {}
    }

}
