package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultPage extends AppCompatActivity {

    private TextView tvCongrats, tvCorrect, tvWrong, tvScore, tvStreak;
    private Button btnPlayAgain, btnExit;

    private User currentUser;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        // --- Ánh xạ view ---
        tvCongrats = findViewById(R.id.congrats_text_view);
        tvCorrect = findViewById(R.id.correct_answers_text_view);
        tvWrong = findViewById(R.id.wrong_answers_text_view);
        tvScore = findViewById(R.id.tvScore);
        tvStreak = findViewById(R.id.tvStreak);

        btnPlayAgain = findViewById(R.id.play_again_button);
        btnExit = findViewById(R.id.exit_button);

        // --- Firebase ---
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // --- Lấy User hiện tại từ session ---
        currentUser = UserSession.getInstance().getUser();

        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // --- Lấy dữ liệu session từ Intent ---
        int sessionScore = getIntent().getIntExtra("SESSION_SCORE", 0);
        int sessionCorrect = getIntent().getIntExtra("SESSION_CORRECT", 0);
        int sessionWrong = getIntent().getIntExtra("SESSION_WRONG", 0);
        int sessionMaxStreak = getIntent().getIntExtra("SESSION_MAX_STREAK", 0);

        // --- Cập nhật User object ---
        currentUser.setScore(currentUser.getScore() + sessionScore);
        currentUser.setCorrect(currentUser.getCorrect() + sessionCorrect);
        currentUser.setWrong(currentUser.getWrong() + sessionWrong);
        currentUser.setTotalQuestions(currentUser.getTotalQuestions() + sessionCorrect + sessionWrong);
        if (sessionMaxStreak > currentUser.getMaxStreak()) {
            currentUser.setMaxStreak(sessionMaxStreak);
        }

        // --- Lưu lên Firebase ---
        dbRef.child(currentUser.getUid()).setValue(currentUser)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cập nhật thành tích thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật dữ liệu!", Toast.LENGTH_SHORT).show());

        // --- Hiển thị dữ liệu ---
        tvCorrect.setText("Correct Answers: " + sessionCorrect);
        tvWrong.setText("Wrong Answers: " + sessionWrong);
        tvScore.setText("Score: " + sessionScore);
        tvStreak.setText("Max Streak: " + sessionMaxStreak);

        // --- Buttons ---
        btnPlayAgain.setOnClickListener(v -> {
            Toast.makeText(this, "Restarting Quiz...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ResultPage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnExit.setOnClickListener(v -> finishAffinity());
    }
}
