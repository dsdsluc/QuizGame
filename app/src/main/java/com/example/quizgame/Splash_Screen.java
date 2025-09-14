package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {

    private ImageView image;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        image = findViewById(R.id.imageViewSplash);
        title = findViewById(R.id.textViewSplash);

        // Load animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_anmin);
        image.startAnimation(animation);
        title.startAnimation(animation);

        // Chuyển sang LoginPage sau 3 giây
        new Handler().postDelayed(() -> {
            Intent i = new Intent(Splash_Screen.this, LoginPage.class);
            startActivity(i);
            finish();
        }, 3000);
    }
}
