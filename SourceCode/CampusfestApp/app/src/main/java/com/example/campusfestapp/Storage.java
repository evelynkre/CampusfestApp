package com.example.campusfestapp;

import android.content.Context;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Klasse zum Speichern und Laden von Daten in und aus dem Internal Storage.
 * Der Internal Storage ist ein Speicherbereich, der der App vom Betriebssystem (Android)
 * zur Verfuegung gestellt wird. Dieser Speicherbereich ist nicht fuer andere Apps oder den
 * Benutzer einsehbar (ausser der Nutzer hat Root Zugriff)
 * Alle Daten in unserem Internal Storage sind im JSON Format gehalten!
 */
public class Storage extends AppCompatActivity {
    //Der Context (die Aktivitaet) von der aus die Klasse aufgerufen wurde
    private final Context context;

    public Storage(Context context){
        this.context = context;
    }

    /**
     * Gibt alle Daten einer Datei im Internal Storage zurueck
     * @param fileName Name der Datei, die geladen werden soll
     * @return die inhalte der Datei (oder null, wenn die Datei nicht gelesen werden konnte)
     */
    protected String getData(String fileName){
        //Pruefung, ob der Dateiname correct ist
        if(fileName == null || fileName.length() == 0){
            return null;
        }
        //Wenn der Dateiname nicht aus ".json" endet, wird diese automatisch hinzugefuegt
        if(!fileName.endsWith(".json")){
            fileName = fileName + ".json";
        }
        try{
            //Oeffnet einen Stream der Datei
            FileInputStream fis = context.openFileInput(fileName);
            //Oeffnet einen StreamReader der den Stream einlesen kann
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            //Noch irgendein Reader (der den String mit einem Buffer einliest)
            BufferedReader bufferedReader = new BufferedReader(isr);
            //StringBuilder wird genutzt, um einen String zusammenzubauen
            StringBuilder sb = new StringBuilder();
            String line;
            //So lange die Datei noch Daten zum einlesen hat, werden diese dem Strinbuilder hinzugefuegt.
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            //Der Zusammengefuehrte String wird zurueckgegeben.
            return sb.toString();
        } catch (FileNotFoundException e) {}
        catch (UnsupportedEncodingException e) {}
        catch (IOException e) {}
        return null;
    }

    /**
     * Gibt ein JSON Object eines JSON Arrays (Ansammlung mehrere JSON Objecte in einem Array) zurueck.
     * Das JSON Object wird anhand eines Schluessels und einem passenden matchingValue gefunden.
     * @param fileName Name der Datei
     * @param key Schluessel
     * @param matchingValue
     * @param matchingValue Falls der Wert einen bestimmten String enthalten soll, kann dieser hier
     *                      mitgegeben werden
     * @return JSON Object (oder null, wenn das JSON Object nicht gefunden wurde)
     */
    protected JSONObject getJsonFromList(String fileName, String key, String matchingValue){
        try {
            //Es wird geprueft, ob ein Key uebergeben wurde
            if(key == null || key.length() == 0){
                return null;
            }
            //Laedt die komplette Datei aus dem Internal Storage
            String loaded = getData(fileName);
            //Prueft, ob die Datei geladen wurde
            if(loaded == null || loaded.length() == 0){
                return null;
            }

            JSONArray array = null;
            try {
                //Versucht ein JSON Array aus den geladenen Daten zu erzeugen.
                array = new JSONArray(loaded);
            }catch(JSONException e){
                //Ist es nicht moeglich ein JSON Array zu erzeugen, so hat die Datei nur ein
                //JSON Object, dass sofort zurueckgegeben werden kann.
                return new JSONObject(loaded);
            }
            if(array==null){
                return null;
            }
            //Hier wird mit einer Schleife das richtige JSON Object in dem JSON Array gesucht
            for(int i=0; i < array.length() ; i++){
                try {
                    //Es wird ein JSON Object aus dem JSON Array gezogen.
                    JSONObject obj = array.getJSONObject(i);
                    //Wenn ein MatchingValue uebergeben wurde, wird geprueft, ob der uebergebene
                    //Wert zu dem Wert des uebergebenen Schluessels passt.
                    if(matchingValue != null && matchingValue.length() > 0 ){
                        if(obj.getString(key).contains(matchingValue)) {
                            return obj;
                        }else{
                            continue;
                        }
                    }else if(obj.has(key)){
                        //Wenn kein MatchingKey uebergeben wurde und das Object den Key enthaelt,
                        //wird das Object zurueckgegeben.
                        return obj;
                    }
                } catch (JSONException e) {}
            }
        } catch (JSONException e) {}
        return null;
    }

    /**
     * Schreibt Daten in eine Datei
     * @param fileName Datei in die geschrieben werden soll
     * @param data Daten, die in die Datei geschrieben werden
     */
    private void setData(String fileName, byte[] data){
        try {
            try {
                //Wenn das File bereits existiert wird dieses zuerst geloescht
                context.deleteFile(fileName);
            }catch(Exception e){}
            //Hier wird ein Stream fuer die Datei erzeugt, der beschrieben werden kann
            FileOutputStream out = context.openFileOutput(fileName, MODE_PRIVATE);
            //Die Datei wird beschrieben
            out.write(data);
            //Die Datei wird geschlossen.
            out.close();
        } catch (IOException e) {}
    }

    /**
     * Setzt Daten von einem speziell praeparierten Rest Response (RueckgabeDaten bei einem Serveraufruf)
     * Hierfuer muss die uebergebene response eine "collection" haben, die zum Dateinamen wird
     * und ein feld "data" das die Daten der Datei enthaelt.
     * @param response JSON Response von einem Rest Call an unseren Server
     * @return Gibt das erzeugte JSON Object zurueck
     */
    protected JSONObject setDataFromRest(String response){
        JSONObject obj = null;
        try {
            //Erzeugt ein neues JSON Object aus der Response
            obj = new JSONObject(response);
            //Hier wird der Dateiname aus der Response geholt
            String fileName = obj.getString("collection");
            //Hier werden die Daten fuer die Datei aus dem Response geholt
            byte[] dataBytes = obj.getString("data").getBytes("UTF-8");
            //Wenn der Dateiname nicht mit ".json" endet. wird die Endung automatisch hinzugefuegt.
            if (!fileName.endsWith(".json")) {
                fileName = fileName + ".json";
            }
            try {
                //Wenn die Datei schon existiert, wird diese geloescht.
                context.deleteFile(fileName);
            }catch(Exception e){}
            //Schreibt die Daten in die Datei
            setData(fileName,dataBytes);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }

    /**
     * @return Gibt zurueck, ob Files im Internal Storage gespeichert wurden
     */
    public boolean hasData(){
        return context.getFilesDir().listFiles().length > 0;
    }
}
