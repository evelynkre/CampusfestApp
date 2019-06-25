package com.example.campusfestapp;

import android.content.Context;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.NetworkResponse;

/**
Erzeugt ein Alarm Fenster, das aufpoppt und vom User geschlossen werden kann.
Als Singleton, damit nicht jedes Mal ein neuer Alert erzeugt werden muss, da sowieso nur
ein Alert gleichzeitig gezeigt werden kann.
 */
public class AlertSingleton {
    private static final AlertSingleton instance = new AlertSingleton();
    private Context context;

    public static AlertSingleton getInstance(Context context) {
        instance.context = context;
        return instance;
    }

    private AlertSingleton() {}

    /**
    Erzeugt den ein Fenster mit dem Titel und der Message.
    Die Title und Messages stehen in der res/values/strings.xml.
    Es werden nur die Ids (int) zu diesen Strings weitergegeben.
     */
    public void showAlert(@StringRes int rTitle, @StringRes int rMessage){
        try{
            //Holt den Titel fuer den Alert aus der Resource XML
            String title = context.getResources().getString(rTitle);
            //Holt die Message fuer den Alert aus der Resource XML
            String message = context.getResources().getString(rMessage);
            //Zeigt den Alert an.
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }catch(Exception e){
            return;
        }
    }

    /**
     Erzeugt den ein Fenster mit dem Titel und der Message.
     Die Title und Messages stehen in der res/values/strings.xml.
     */
    public void showAlert(@StringRes int rTitle, String message){
        try{
            //Holt den Titel fuer den Alert aus der Resource XML
            String title = context.getResources().getString(rTitle);
            //Zeigt den Alert an.
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }catch(Exception e){
            return;
        }
    }

    /**
     Erzeugt den ein Fenster mit dem Titel und der Message.
     Die Title und Messages stehen in der res/values/strings.xml.
     */
    public void showAlert(@StringRes int rTitle, NetworkResponse response){
        try{
            //Holt den Titel fuer den Alert aus der Resource XML
            String title = context.getResources().getString(rTitle);
            String responseMessage = getResponseMessage(response.statusCode);
            if(responseMessage == null){
                return;
            }
            //Zeigt den Alert an.
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(getResponseMessage(response.statusCode))
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }catch(Exception e){
            return;
        }
    }

    private String getResponseMessage(int statusCode){
        switch(statusCode){
            case 204:
                return "Angaben sind nicht korrekt.";
            case 401:
                return "Der Vorgang konnte nicht abgeschlossen werden. (Bezahltokens wurden nicht aktiviert oder es fehlen Berechtigungen)";
            case 500:
                return "Es besteht keine Internetverbindung.";
        }
        return "Unbekannter Fehler";
    }
}
