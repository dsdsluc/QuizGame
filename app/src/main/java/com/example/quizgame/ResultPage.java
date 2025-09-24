package com.example.quizgame;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        // Handle Edge-to-Edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase setup
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Lấy số câu đúng / sai
        int correctCount = getIntent().getIntExtra("correctCount", 0);
        int wrongCount = getIntent().getIntExtra("wrongCount", 0);

        // Nếu user != null thì update score
        if (user != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            // Cộng thêm điểm = số câu đúng
            userRef.child("score").get().addOnSuccessListener(snapshot -> {
                int currentScore = 0;
                if (snapshot.exists()) {
                    currentScore = snapshot.getValue(Integer.class);
                }
                int newScore = currentScore + correctCount;
                userRef.child("score").setValue(newScore);
            });
        }

        // Ánh xạ TextView hiển thị kết quả
        TextView correctTextView = findViewById(R.id.correct_answers_text_view);
        TextView wrongTextView = findViewById(R.id.wrong_answers_text_view);
        correctTextView.setText("Correct Answer: " + correctCount);
        wrongTextView.setText("Wrong Answer: " + wrongCount);

        // Ánh xạ icon
        ImageView icon1 = findViewById(R.id.icon1);
        ImageView icon2 = findViewById(R.id.icon2);
        ImageView icon3 = findViewById(R.id.icon3);

        // --- Animation xoay ---
        ObjectAnimator rotate1 = ObjectAnimator.ofFloat(icon1, "rotation", 0f, 360f);
        rotate1.setDuration(1500);
        rotate1.setRepeatCount(ValueAnimator.INFINITE);
        rotate1.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator rotate2 = ObjectAnimator.ofFloat(icon2, "rotation", 0f, -360f);
        rotate2.setDuration(1500);
        rotate2.setRepeatCount(ValueAnimator.INFINITE);
        rotate2.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator rotate3 = ObjectAnimator.ofFloat(icon3, "rotation", 0f, 360f);
        rotate3.setDuration(1500);
        rotate3.setRepeatCount(ValueAnimator.INFINITE);
        rotate3.setInterpolator(new AccelerateDecelerateInterpolator());

        rotate1.start();
        rotate2.start();
        rotate3.start();

        // --- Animation nhấp nháy ---
        ObjectAnimator blink1 = ObjectAnimator.ofFloat(icon1, "alpha", 1f, 0.5f);
        blink1.setDuration(700);
        blink1.setRepeatMode(ValueAnimator.REVERSE);
        blink1.setRepeatCount(ValueAnimator.INFINITE);
        blink1.start();

        ObjectAnimator blink2 = ObjectAnimator.ofFloat(icon2, "alpha", 1f, 0.5f);
        blink2.setDuration(700);
        blink2.setRepeatMode(ValueAnimator.REVERSE);
        blink2.setRepeatCount(ValueAnimator.INFINITE);
        blink2.start();

        ObjectAnimator blink3 = ObjectAnimator.ofFloat(icon3, "alpha", 1f, 0.5f);
        blink3.setDuration(700);
        blink3.setRepeatMode(ValueAnimator.REVERSE);
        blink3.setRepeatCount(ValueAnimator.INFINITE);
        blink3.start();

        // --- Button ---
        Button playAgainButton = findViewById(R.id.play_again_button);
        Button exitButton = findViewById(R.id.exit_button);

        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultPage.this, Quiz_Page.class);
            startActivity(intent);
            finish();
        });

        exitButton.setOnClickListener(v -> {
            finishAffinity(); // Thoát app
        });
    }
}
