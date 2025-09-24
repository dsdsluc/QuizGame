package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen extends AppCompatActivity {

    private ImageView image;
    private TextView title;
    private static final int SPLASH_DELAY = 3000; // 3 giây

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

        // Delay 3s rồi check user
        new Handler().postDelayed(() -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null && UserSession.getInstance().getUser() != null) {
                // Đã login và có user trong session → vào Main
                Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
                startActivity(intent);
            } else {
                // Chưa login → vào Login
                Intent intent = new Intent(Splash_Screen.this, LoginPage.class);
                startActivity(intent);
            }
            finish();
        }, SPLASH_DELAY);
    }
}
