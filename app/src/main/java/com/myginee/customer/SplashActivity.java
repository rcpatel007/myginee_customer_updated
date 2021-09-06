package com.myginee.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.myginee.customer.activity.SignInActivity;
import com.myginee.customer.utils.GineePref;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    VideoView videoSplash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        videoSplash = findViewById(R.id.videoSplash);

        GineePref.getSharedPreferences(SplashActivity.this);
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        /*new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                *//* Create an Intent that will start the Menu-Activity. *//*
                if(!GineePref.getUSERID(SplashActivity.this).equals("")){
                    Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    if(getIntent().getStringExtra("orderId") != null){
                        mainIntent.putExtra("orderId", getIntent().getStringExtra("orderId"));
                    }
                    startActivity(mainIntent);
                    finish();
                }else {
                    Intent mainIntent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);*/

        try {
//            VideoView videoHolder = new VideoView(this);
//            setContentView(videoHolder);
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ginee_splash);
            videoSplash.setVideoURI(video);

            videoSplash.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    jump();
                }
            });
            videoSplash.start();
        } catch (Exception ex) {
            jump();
        }
    }

    private void jump() {
        if (isFinishing())
            return;
//        if(!GineePref.getUSERID(SplashActivity.this).equals("")){
            Intent mainIntent;
            if(getIntent().getStringExtra("type") == null){
                mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
            }else {
                mainIntent = new Intent(SplashActivity.this, ChatActivity.class);
                mainIntent.putExtra("_id", getIntent().getStringExtra("order"));
                mainIntent.putExtra("agent_id", getIntent().getStringExtra("agent_id"));
            }
            startActivity(mainIntent);
            finish();
       /* }else {
            Intent mainIntent = new Intent(SplashActivity.this, SignInActivity.class);
            startActivity(mainIntent);
            finish();
            Intent mainIntent = new Intent(SplashActivity.this, SignInActivity.class);
            startActivity(mainIntent);
            finish();
        }*/
    }


}
