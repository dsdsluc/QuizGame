package com.example.quizgame;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;


import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import nl.dionsegijn.konfetti.core.PartyFactory;


public class ResultPage extends AppCompatActivity {

    private LinearLayout leaderboardContainer;
    private DatabaseReference usersRef;
    private KonfettiView konfettiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        // Nhận dữ liệu từ QuizPage
        int score = getIntent().getIntExtra("score", 0);
        int correct = getIntent().getIntExtra("correct", 0);
        int wrong = getIntent().getIntExtra("wrong", 0);
        int timeTaken = getIntent().getIntExtra("totalTime", 0);

        Button btnHome = findViewById(R.id.btnHome);
        Button btnExit = findViewById(R.id.btnExit);
        // Ánh xạ view
        TextView txtScore = findViewById(R.id.textScore);
        TextView txtOutOf = findViewById(R.id.textOutOf);
        TextView txtPoints = findViewById(R.id.textPoints);
        TextView txtTime = findViewById(R.id.textTime);
        leaderboardContainer = findViewById(R.id.leaderboardContainer);
        konfettiView = findViewById(R.id.konfettiView);

        // Firebase reference
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Animation điểm số
        ValueAnimator scoreAnim = ValueAnimator.ofInt(0, score);
        scoreAnim.setDuration(1000);
        scoreAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scoreAnim.addUpdateListener(animation ->
                txtScore.setText(String.valueOf(animation.getAnimatedValue())));
        scoreAnim.start();

        // Set dữ liệu văn bản
        txtOutOf.setText("Out of " + (correct + wrong));
        txtPoints.setText(score + " Points");
        txtTime.setText("You took " + formatTime(timeTaken) + " to complete the quiz");

        // Nút Trang chủ -> mở MainActivity
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultPage.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

// Nút Thoát -> thoát app
        btnExit.setOnClickListener(v -> {
            finishAffinity(); // Đóng toàn bộ activity và thoát app
            System.exit(0);   // Thoát hoàn toàn tiến trình (optional)
        });

        // Hiển thị leaderboard
        showLeaderboardNeighbors();

        // Chạy confetti khi hoàn thành quiz
        startConfetti();
    }

    private void showLeaderboardNeighbors() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersRef.get().addOnSuccessListener(snapshot -> {
            List<User> allUsers = new ArrayList<>();

            for (DataSnapshot userSnap : snapshot.getChildren()) {
                User user = userSnap.getValue(User.class);
                if (user != null) {
                    allUsers.add(user);
                }
            }

            // Sắp xếp theo score giảm dần
            Collections.sort(allUsers, (u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()));

            // Gán rank
            for (int i = 0; i < allUsers.size(); i++) {
                allUsers.get(i).setRank(i + 1);
            }

            // Tìm vị trí người chơi hiện tại
            int myIndex = -1;
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getUid().equals(currentUid)) {
                    myIndex = i;
                    break;
                }
            }

            // Lấy hàng xóm
            List<User> neighbors = new ArrayList<>();
            if (myIndex != -1) {
                if (myIndex - 1 >= 0) neighbors.add(allUsers.get(myIndex - 1));
                neighbors.add(allUsers.get(myIndex));
                if (myIndex + 1 < allUsers.size()) neighbors.add(allUsers.get(myIndex + 1));
            }

            // Render ra UI
            leaderboardContainer.removeAllViews();
            for (User u : neighbors) {
                View itemView = getLayoutInflater().inflate(R.layout.item_leaderboard, leaderboardContainer, false);
                TextView txtName = itemView.findViewById(R.id.textName);
                TextView txtPoints = itemView.findViewById(R.id.textPoints);

                txtName.setText("#" + u.getRank() + " " + u.getFullName());
                txtPoints.setText(u.getScore() + " pts");

                leaderboardContainer.addView(itemView);
            }
        });
    }

    private void startConfetti() {
        EmitterConfig emitterConfig = new Emitter(2, TimeUnit.SECONDS).perSecond(100);

        Party party = new PartyFactory(emitterConfig)
                .spread(360) // tỏa tròn
                .angle(Angle.TOP) // bắn từ trên xuống
                .position(new Position.Relative(0.5, 0.3)) // vị trí giữa màn hình
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .build();

        konfettiView.start(party);
    }



    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return minutes + " min " + secs + " sec";
    }
}
