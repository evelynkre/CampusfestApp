package com.example.campusfestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
Hauptmenue der App.
Wenn der Nutzer bestimmte Rechte inne hat (Mitarbeiter oder Verkaufer),
dann werden ihm weitere Menuepunkte angezeigt.
 */
public class MainMenu extends AppCompatActivity {
    private Storage storage;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Falls die Aktivitaet vorher bereits geoffnet war, kann man dieser einen Status mitgeben.
        //(Bspws. ScrollPosition oder aehnliches)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        storage = new Storage(this);
        queue = Volley.newRequestQueue(this);
    }

    /**
    Oeffnet die LineUp Aktivitaet
     */
    public void openLineUp(View view){
        Intent lineUp = new Intent(this, LineUp.class);
        startActivity(lineUp);
    }

    /**
    Oeffnet die Timetable Aktivitaet
     */
    public void openTimetable(View v){
        Intent timetable = new Intent(this, Timetable.class);
        startActivity(timetable);
    }
}
