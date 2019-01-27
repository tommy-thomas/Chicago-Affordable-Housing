package org.affordablehousing.chi.housingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.affordablehousing.chi.housingapp.ui.MapsActivity;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView logo;
    private static int splashTimeOut=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo=(ImageView)findViewById(R.id.logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this,MapsActivity.class);
                startActivity(i);
                finish();

                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        },splashTimeOut);

        Animation splash_anim = AnimationUtils.loadAnimation(this,R.anim.splashanimation);
        logo.startAnimation(splash_anim);
    }
}
