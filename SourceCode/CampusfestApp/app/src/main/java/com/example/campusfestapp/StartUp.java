package com.example.campusfestapp;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
Holt alle Daten vom Server, sofern eine Internetverbindung besteht.
Die Daten werden dann im Internal Storage gespeichert auf den jede Aktivitaet der eigenen App
zugreifen kann. Der Internal Storage ist nur fuer die eigene App lesbar und nur User mit
Root Zugriff koennen am Smartphone auf diesen zugreifen.
 */
public class StartUp extends AppCompatActivity {
    //Die Serveraddresse muss angepasst werden, da bis jetzt immer ein lokaler Server verwendet wird.
    public static String SERVER_ADDRESS = "http://134.103.242.23:3000";
    //Counter, der die Abfragen an den Server zaehlt, um erst dann weiterzuleiten, wenn alle Abfragen
    //abgeschlossen sind.
    final AtomicInteger requestsCounter = new AtomicInteger(0);
    //Beim ersten Request eingesetzt, da dieser erst alle Daten zureckgibt, die danach geladen werden muessen.
    final AtomicBoolean dataToLoadRequest = new AtomicBoolean(false);
    private Storage storage;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Falls die Aktivitaet vorher bereits geoffnet war, kann man dieser einen Status mitgeben.
        //(Bspws. ScrollPosition oder aehnliches)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        //Initialisieren der Request Queue, die alle Rest Anfragen nacheinander abarbeitet.
        queue = Volley.newRequestQueue(this);
        //Initialisieren des Internal Storage
        storage = new Storage(this);
        requestsCounter.incrementAndGet();
        //Die erste Datei (nach der Id), die vom Server (der Datenbank) geholt wird,
        //ist die Sammlung "DataToLoad", hier stehen alle Verweise auf die Sammlungen,
        //die fuer die Funktionalitaet der App gebraucht werden.
        StringRequest req = new StringRequest(Request.Method.GET, SERVER_ADDRESS + "/datatoload",
                response -> {
                    //Die Daten die vom Server kommen werden in den Internal Storage geschrieben
                    storage.setDataFromRest(response);
                    JSONArray toLoad = null;
                    try {
                        toLoad = new JSONArray(storage.getData("DataToLoad"));
                        //Hier wird alles durchlaufen, was in der Sammlung "DataToLoas" definiert ist
                        //und nacheinander vom Server (der Datenbank) gezogen.
                        for(int i=0; i < toLoad.length(); i++){
                            try {
                                //Bei jedem neuen Request wird der Counter um eins erhoeht.
                                requestsCounter.incrementAndGet();
                                getRestCall("/"+toLoad.getJSONObject(i).getString("restUri"));
                            } catch (JSONException e) {}
                        }
                    } catch (JSONException e) {
                        //Wenn kein Internet besteht wird versucht ohne aktualisierte Daten die App
                        //zu starten.
                        GoToMainMenu();
                    }
                }, error -> {
            //Wenn kein Internet besteht wird versucht ohne aktualisierte Daten die App
            //zu starten.
            GoToMainMenu();
        });
        //Fuegt den Request der RequestQueue hinzu, erst dann wird dieser auch ausgefuehrt.
        queue.add(req);
        //Wird aufgerufen, wenn ein Request abgeschlossen ist.
        queue.addRequestFinishedListener(request -> {
            //Bei jedem abgearbeiteten Request (Response erhalten oder Timeout) wird der Counter
            //um eins gesenkt.
            requestsCounter.decrementAndGet();
            //Wenn der Counter 0 erreicht hat und die erste Request (GET "DataToLoad") abgeschlossen
            //ist, wird auf das Hauptmenue weitergeleitet.
            if (dataToLoadRequest.get() && requestsCounter.get() == 0) {
                GoToMainMenu();
            }
        });
    }

    /**
    Macht einen Rest Call an den Server mit der GET Methode (Daten stehen in der Url)
     */
    private void getRestCall(String restUri){
        StringRequest req = new StringRequest(Request.Method.GET, SERVER_ADDRESS + restUri,
                response -> {
                    //Speichert die zurueckgegebenen Daten vom Server im Internal Storage
                    storage.setDataFromRest(response);
                    //Sobald der erste Request erfolgreich abgeschlossen ist, wird der Boolean auf
                    //False gesetzt, nun kann der Counter uebernehmen. Sobald dieser 0 erreicht,
                    //wird der Benutzer in das Hauptmenue weitergeleitet.
                    dataToLoadRequest.getAndSet(true);
                },
                error -> {});
        //Fuegt den Request der RequestQueue hinzu, erst dann wird dieser auch ausgefuehrt.
        queue.add(req);
    }

    /**
    Springt in das Hauptmenue der App, sofern alle Daten vom Server erfolgreich geladen wurden
    oder sich bereits Daten im Internal Storage befinden.
     */
    private void GoToMainMenu(){
        if(storage.hasData()) {
            //Startet die MainMenu Aktivitaet
            Intent MainMenu = new Intent(this, MainMenu.class);
            startActivity(MainMenu);
            //Sorgt dafuer, dass der User nach dem Ladescreen nicht wieder vom MainMenu auf diesen
            //zurueckkehren kann.
            finish();
        }else{
            //Wenn keine Daten im Internal Storage gespeichert wurden, wird eine Warnmeldung ausgegeben
            //und die App beendet.
            finishAndRemoveTask();
        }
    }
}
