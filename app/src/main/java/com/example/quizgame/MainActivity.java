package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnStart,  btnLeaderboard, btnAchievements;
    private ImageView imgLogout;
    private TextView txtWelcome, txtScore, tvLevel, tvRank;
    private Spinner spinnerGameMode;
    private ProgressBar progressLoading;

    private AuthHelper authHelper;
    private String selectedMode = "Cơ bản";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Init view ---
        btnStart = findViewById(R.id.btnStart);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        btnAchievements = findViewById(R.id.btnAchievements);
        imgLogout = findViewById(R.id.imgLogout);
        txtWelcome = findViewById(R.id.txtWelcome);
        txtScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.tvLevel);
        tvRank = findViewById(R.id.tvRank);
        spinnerGameMode = findViewById(R.id.spinnerGameMode);
        progressLoading = findViewById(R.id.progressLoading);

        // Init AuthHelper
        authHelper = new AuthHelper();

        // Lấy user từ session
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            txtWelcome.setText("Xin chào, " + currentUser.getFullName());
            txtScore.setText("Điểm: " + currentUser.getScore());
            tvLevel.setText("Level: " + currentUser.getLevel());
            tvRank.setText("Hạng: " + currentUser.getRank());
        } else {
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
            finish();
        }

        // --- Setup Spinner ---
        String[] gameModes = {"Classic", "Sinh tồn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, gameModes);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGameMode.setAdapter(adapter);
        spinnerGameMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMode = gameModes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMode = "Classic";
            }
        });

        // --- Load Question từ Firebase ---
        loadQuestionsFromFirebase();

        // --- Btn Start ---
        btnStart.setOnClickListener(v -> {
            User user = UserSession.getInstance().getUser();
            if (user != null) {
                user.resetQuiz(selectedMode); // topic = null
            }
            Intent intent = new Intent(MainActivity.this, Quiz_Page.class);
            intent.putExtra("GAME_MODE", selectedMode);
            startActivity(intent);
        });
        btnLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Leaderboard.class);
            startActivity(intent);
        });

        btnAchievements.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MatchHistoryActivity.class);
            startActivity(intent);
        });


        // --- Logout ---
        imgLogout.setOnClickListener(v -> {
            authHelper.logout();
            UserSession.getInstance().clear();
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    // --- Hàm load Question ---
    private void loadQuestionsFromFirebase() {
        DatabaseReference questionsRef = FirebaseDatabase.getInstance().getReference("Questions");

        // Hiển thị spinner và disable nút Play
        progressLoading.setVisibility(View.VISIBLE);
        btnStart.setEnabled(false);

        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                QuestionManager.getInstance().clearAll();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Question q = ds.getValue(Question.class);
                    if (q != null) {
                        QuestionManager.getInstance().addQuestion(q);
                    }
                }
                progressLoading.setVisibility(View.GONE);
                btnStart.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressLoading.setVisibility(View.GONE);
                btnStart.setEnabled(true);
                Toast.makeText(MainActivity.this, "Load câu hỏi thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
