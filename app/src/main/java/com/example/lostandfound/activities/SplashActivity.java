package com.example.lostandfound.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.R;

public class SplashActivity extends AppCompatActivity {

    // FIX: hold named references so we can cancel in onDestroy
    private final Handler  handler      = new Handler(Looper.getMainLooper());
    private final Runnable navigateNext = this::goToNextScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Fade + slide up animation on logo area
        LinearLayout logoArea = findViewById(R.id.logoArea);

        AlphaAnimation     fade  = new AlphaAnimation(0f, 1f);
        TranslateAnimation slide = new TranslateAnimation(0, 0, 60, 0);
        AnimationSet       set   = new AnimationSet(true);
        set.addAnimation(fade);
        set.addAnimation(slide);
        set.setDuration(900);
        set.setFillAfter(true);
        logoArea.startAnimation(set);

        handler.postDelayed(navigateNext, 2200);
    }

    private void goToNextScreen() {
        SharedPreferences prefs = getSharedPreferences("LFSession", MODE_PRIVATE);
        boolean loggedIn = prefs.getBoolean("loggedIn", false);

        Intent intent = new Intent(this, loggedIn ? HomeActivity.class : LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        // FIX: cancel the pending callback so it never fires on a destroyed Activity
        handler.removeCallbacks(navigateNext);
        super.onDestroy();
    }
}