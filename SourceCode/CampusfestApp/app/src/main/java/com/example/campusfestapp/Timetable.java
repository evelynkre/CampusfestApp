package com.example.campusfestapp;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.example.campusfestapp.ui.timetable.TimetablePagerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Die Timetable Aktivitaet zeigt Timetables fuer die verschiedenen Buehnen auf dem Campusfest.
 * Es koennen dynamisch mehr oder weniger Buehnen in der Sammlung "Timetables" in der
 * Datenbank generiert werden, die dann angezeigt werden.
 * Die Namen der Kuenstler sind klickbar und verweisen auf die Artist Aktivitaet
 */
public class Timetable extends AppCompatActivity {
    private Storage storage;
    private JSONArray timetables;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Falls die Aktivitaet vorher bereits geoffnet war, kann man dieser einen Status mitgeben.
        //(Bspws. ScrollPosition oder aehnliches)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        //Hier wird der Internal Storage initialisiert
        storage = new Storage(this);
        try {
            //Hier werden alle Zeitplaene aus dem Internal Storage geladen
            timetables = new JSONArray(storage.getData("Timetables"));
            //Hier wird der ein Adapter erzeugt, der beim Wechseln der Tabs die entsprechende Seite anzeigt.
            TimetablePagerAdapter timetablePagerAdapter = new TimetablePagerAdapter(this, getSupportFragmentManager(), timetables);
            //Eine View, die es dem Benutzer erlaubt nach links und rechts durch Seiten zu springen
            ViewPager viewPager = findViewById(R.id.view_pager);
            //Der Adapter wird auf den ViewPager gesetzt.
            viewPager.setAdapter(timetablePagerAdapter);
            //Das Layout, dass die Tabs enthaelt wird geladen.
            TabLayout tabs = findViewById(R.id.tabs);
            //Die Tabs werden ebenfalls an den Viewpager gebunden.
            tabs.setupWithViewPager(viewPager);
            //Hier wird ueber alle vorhandenen Zeitplan gegangen, um diese den Seiten hinzuzufuegen.
            for (int i = 0; i < timetables.length(); i++) {
                try {
                    //Hier wird der naechste Buehne-Zeitplan-Datensatz aus den Daten vom Internal Storage geholt.
                    JSONObject stageTimetable = timetables.getJSONObject(i);
                    //Der Buehnenname wird aus den Daten des Buehne-Zeitplan-Datensatz geholt
                    String stageName = stageTimetable.getString("stage");
                    //Im TabLayout wird ein neuer Tab generiert fuer jeden Buehne-Zeitplan-Datensatz
                    TabLayout.Tab tab = tabs.newTab();
                    //Der Tab bekommt die Buehnenbezeichnung als Titel
                    tab.setText(stageName);
                } catch (JSONException e) {}
            }
        } catch (JSONException e) {
            finish();
        }
    }


    /**
     * Wenn ein Kuenstler im Zeitplan angeklickt wird, wird diese Methode aufgerufen.
     * Die Methode startet die Artist Aktivitaet und uebergibt dieser den angeklickten Kuenstler.
     * @param v geklicktes TextView
     */
    public void textClick(View v){
        //Erzeugt ein neues Intent der Artist Aktivitaet
        Intent Artist = new Intent(this, Artist.class);
        //Holt den Kuenstlernamen aus dem geklickten TextView
        String artistName = ((TextView)v).getText().toString().trim();
        //Speichert den Kuenstlernamen in das Intent, damit die Artist Aktivitaet diesen verwenden kann.
        Artist.putExtra("artistName", artistName);
        //Startet die Artist Aktivitaet.
        startActivity(Artist);
    }
}