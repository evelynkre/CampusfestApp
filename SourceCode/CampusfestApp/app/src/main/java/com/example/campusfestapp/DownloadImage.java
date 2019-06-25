package com.example.campusfestapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Hilfsklasse, um Bilder dynamisch von einer URL zu laden.
 * Hierfuer wird ein AsyncTask benutzt, der das Bild im Hintergrund laedt, ohne dass die Hauptapp
 * blockiert wird. (Das fuehrt dazu, dass Android denkt, die App haette sich aufgehaengt)
 */
public class DownloadImage extends AsyncTask<String, Void, Bitmap>{
    ImageView bmImage;

    /**
     * Konstruktor
     * @param bmImage ImageView auf die das geladene Bild gesetzt wird, sobald es geladen wurde.
     */
    public DownloadImage(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    /**
     * Laedt das Bild ueber einen statischen Link vom Server und fuegt es der ImageView hinzu.
     * @param uris Uris zu den zu ladenden Bildern (bspws: "/logos/Bastille.png")
     * @return Das dekodierte Bild zum einsetzen in die ImageView
     */
    protected Bitmap doInBackground(String... uris) {
        //Erzeugt die Url aus der ServerAdresse aus StartUp und der uebergebenen Uri.
        String Url = StartUp.SERVER_ADDRESS + uris[0];
        Bitmap image = null;
        try {
            //Oeffnet einen Stream mit der Url des Bildes
            InputStream in = new java.net.URL(Url).openStream();
            //Die BitmapFactore dekodiert den Stream, damit dieser in die ImageView gesetzt
            //und angezeigt werden kann.
            image = BitmapFactory.decodeStream(in);
        } catch (Exception e) {}
        return image;
    }

    /**
     * Wenn Das Bild im Hintergrund erfolgreich geladen wurde, wird es in die ImageView eingesetzt,
     * und angezeigt.
     * @param decodedImage Das dekodierte Bild vom Stream fuer die ImageView
     */
    protected void onPostExecute(Bitmap decodedImage) {
        bmImage.setImageBitmap(decodedImage);
    }
}
