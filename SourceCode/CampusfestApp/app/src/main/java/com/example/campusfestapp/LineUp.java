package com.example.campusfestapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Die LineUp Aktivitaet zeigt die Kuenstler, die auf dem Campusfest auftreten.
 * Die Namen der Kuenstler werden je populaerer sie sind, desto groesser und weiter oben dargestellt
 * in der Aktivitaet dargestellt. Diese Textdarstellung passiert automaitsch anhand der in der
 * Datenbank hinterlegten Popularitaetsgrade (int).
 */
public class LineUp extends AppCompatActivity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_up);
        linearLayout = findViewById(R.id.artists);
        setArtistsByPopularity();
    }

    /**
     * Die Kuenstler werden nach ihrer Popularitaet angeordnet dargstellt.
     * Wenn der Kuenstler weniger populaer ist, wird er kleiner und weiter unten dargestellt.
     */
    private void setArtistsByPopularity(){
        JSONArray artists = null;
        try {
            //Die Kuenstler werden aus dem Internal Storage geholt.
            artists = new JSONArray(new Storage(this).getData("Artists"));
            int popularity = 1;
            while(artists.length() > 0){
                for(int i = 0; i < artists.length();){
                    try {
                        String artistName = artists.getJSONObject(i).getString("name");
                        int artistPopularity = Integer.parseInt(artists.getJSONObject(i).getString("popularity"));
                        if (artistPopularity == popularity) {
                            artists.remove(i);
                            MakeTextField(artistName, popularity);
                        }else{
                            i++;
                        }
                    }catch(Exception e){
                        artists.remove(i);
                    }
                }
                popularity++;
            }
        } catch (JSONException e) {
            finish();
        }
    }

    /**
     * Erstellt ein TextView in das der Kuenstlername eingesetzt wird. Die angezeigte Groesse wird
     * anhand des Popularitaetsgrades angepasst. Je populaerer der Kuenstler, desto groesser und weiter
     * oben ist der Name zu finden.
     * @param artistName Name des Kuenstlers
     * @param popularity Popularitaetsgrad des Kuenstlers
     */
    private void MakeTextField(String artistName, int popularity){
        //Hier wird aus einem Layout eine neue TextView generiert.
        TextView textView = (TextView)getLayoutInflater().inflate(R.layout.template_clickable_text, null);
        //Hier wird der Bandname in die TextView geschrieben.
        textView.setText(artistName);
        //Hier wird die Textgroesse anhand eines initialen Wertes, einem Faktor und dem
        //Popularitaetsgrad errechnet.
        float textSize = getResources().getInteger(R.integer.lineup_text_biggest_size) - (popularity * getResources().getInteger(R.integer.lineup_text_factor));
        //Hier wird die Textgroesse gesetzt
        textView.setTextSize(textSize);
        //Hier wird das generierte TextView dem Layout hinzugefuegt, damit es angezeigt wird.
        linearLayout.addView(textView);
    }

    /**
     * Wird aufgerufen, wenn eine Band angeklickt wird
     * @param v Die TextView auf die geklickt wurde
     */
    protected void textClick(View v){
        //Neues Intent mit der Artist Aktivitaet wird erzeugt.
        Intent Artist = new Intent(this, Artist.class);
        //Dem Intent wird der Kuenstlername mitgegeben, damit die Artist Aktivitaet die richtige Band laedt.
        Artist.putExtra("artistName", ((TextView)v).getText());
        //Artist Aktivitaet wird gestartet.
        startActivity(Artist);
    }
}
