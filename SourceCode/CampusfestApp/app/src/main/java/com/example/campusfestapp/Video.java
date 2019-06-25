package com.example.campusfestapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Spielt Videos ab, die der Aktivitaet per URI uebergeben werden.
 */
public class Video extends AppCompatActivity {
    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Falls die Aktivitaet vorher bereits geoffnet war, kann man dieser einen Status mitgeben.
        //(Bspws. ScrollPosition oder aehnliches)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = findViewById(R.id.videoView);
        //Die URL fuer das Video wird aus der in StartUp definierten ServerAddresse und der aus der Aktivitaet
        //(LineUp oder TimeTable) uebergebenen "videoUri" zusammengebaut
        String videoUrl = StartUp.SERVER_ADDRESS + getIntent().getStringExtra("videoUri");
        Uri video = Uri.parse(videoUrl);
        // Set the media controller buttons
        if (mediaController == null) {
            mediaController = new MediaController(Video.this);
            // Setzt den MediaController visuell in das Video
            mediaController.setAnchorView(videoView);
            // Setzt den MediaController (Play, Pause, Skip Funktionen) auf das Video
            videoView.setMediaController(mediaController);
        }
        try {
            //Setzt die Url auf das Video, von der das Video geladen wird (bsp: www.video.com/blabla.mp4)
            videoView.setVideoURI(video);
        } catch (Exception e) {}
        videoView.requestFocus();
        //Sobald das Video fertig geladen ist, wird dieser Listener aufgerufen.
        videoView.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }
}
