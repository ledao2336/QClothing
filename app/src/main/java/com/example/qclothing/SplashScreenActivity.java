package com.example.qclothing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the splash screen layout (we'll create this XML file next)
        setContentView(R.layout.activity_splash_screen);

        // Delay for SPLASH_SCREEN_DELAY milliseconds and then start MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent to start MainActivity
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Close SplashScreenActivity so user can't go back to it
            }
        }, SPLASH_SCREEN_DELAY);
    }
}