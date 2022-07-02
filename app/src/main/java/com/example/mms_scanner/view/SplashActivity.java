package com.example.mms_scanner.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mms_scanner.R;


public class SplashActivity extends AppCompatActivity {
    ImageView img1, img2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        img1 = findViewById(R.id.logoImage);
        Animation img1Anim = AnimationUtils.loadAnimation(this, R.anim.slide_fade_in);
        img1.startAnimation(img1Anim);
        img2 = findViewById(R.id.splashScreenImage);
        Animation img2Anim = AnimationUtils.loadAnimation(this, R.anim.side_slide);
        img2.startAnimation(img2Anim);

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this,
                    LoginActivity.class);
            startActivity(i);
            finish();
        }, 3000);

    }
}