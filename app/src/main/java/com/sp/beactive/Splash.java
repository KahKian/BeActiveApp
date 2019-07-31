package com.sp.beactive;

import android.content.Intent;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        mediaPlayer = MediaPlayer.create(this, R.raw.splash_sound);
        mediaPlayer.start();
        Thread background = new Thread() {
            public void run() {
                try {

                    sleep(5*1000);

                    Intent i=new Intent(getBaseContext(),SignIn.class);
                    startActivity(i);

                    finish();
                } catch (Exception e) {
                }
            }
        };

        background.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        mediaPlayer.release();
    }
}
