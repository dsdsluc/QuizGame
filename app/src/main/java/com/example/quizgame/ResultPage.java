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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class ResultPage extends AppCompatActivity {

    private LinearLayout leaderboardContainer;
    private KonfettiView konfettiView;

    // dùng repo thay vì gọi Firebase trực tiếp
    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        // 1. Nhận dữ liệu từ QuizPage
        int score        = getIntent().getIntExtra("score", 0);
        int correct      = getIntent().getIntExtra("correct", 0);
        int wrong        = getIntent().getIntExtra("wrong", 0);
        int timeTaken    = getIntent().getIntExtra("totalTime", 0);
        int totalQs      = getIntent().getIntExtra("totalQuestions", -1);

        // 2. Ánh xạ view
        Button btnHome   = findViewById(R.id.btnHome);
        Button btnExit   = findViewById(R.id.btnExit);
        TextView txtScore   = findViewById(R.id.textScore);
        TextView txtOutOf   = findViewById(R.id.textOutOf);
        TextView txtPoints  = findViewById(R.id.textPoints);
        TextView txtTime    = findViewById(R.id.textTime);
        leaderboardContainer = findViewById(R.id.leaderboardContainer);
        konfettiView         = findViewById(R.id.konfettiView);

        // 3. Animation điểm số
        ValueAnimator scoreAnim = ValueAnimator.ofInt(0, score);
        scoreAnim.setDuration(1000);
        scoreAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scoreAnim.addUpdateListener(animation ->
                txtScore.setText(String.valueOf(animation.getAnimatedValue())));
        scoreAnim.start();

        // 4. Set dữ liệu văn bản
        if (totalQs != -1) {
            txtOutOf.setText("Out of " + totalQs);
        } else {
            txtOutOf.setText("Out of " + (correct + wrong));
        }

        txtPoints.setText(score + " Points");
        txtTime.setText("You took " + formatTime(timeTaken) + " to complete the quiz");

        // 5. Nút Trang chủ
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultPage.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // 6. Nút Thoát → show dialog đẹp
        btnExit.setOnClickListener(v -> showExitDialog());

        // 7. Hiển thị leaderboard “láng giềng”
        showLeaderboardNeighbors();

        // 8. Confetti
        startConfetti();
    }

    private void showLeaderboardNeighbors() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRepository.getNeighbors(currentUid, new UserRepository.NeighborCallback() {
            @Override
            public void onSuccess(List<User> neighbors) {
                leaderboardContainer.removeAllViews();
                for (User u : neighbors) {
                    if (u == null) continue;
                    View itemView = getLayoutInflater().inflate(
                            R.layout.item_leaderboard,
                            leaderboardContainer,
                            false
                    );

                    TextView txtName   = itemView.findViewById(R.id.textName);
                    TextView txtPoints = itemView.findViewById(R.id.textPoints);

                    txtName.setText("#" + u.getRank() + " " + u.getFullName());
                    txtPoints.setText(u.getScore() + " pts");

                    leaderboardContainer.addView(itemView);
                }
            }

            @Override
            public void onError(String error) {
                // bạn muốn thì Toast ở đây cũng được
            }
        });
    }

    private void startConfetti() {
        EmitterConfig emitterConfig = new Emitter(2, TimeUnit.SECONDS).perSecond(100);

        Party party = new PartyFactory(emitterConfig)
                .spread(360)
                .angle(Angle.TOP)
                .position(new Position.Relative(0.5, 0.3))
                .colors(Arrays.asList(
                        0xFFFCE18A,
                        0xFFFF726D,
                        0xFFF4306D,
                        0xFFB48DEF
                ))
                .build();

        konfettiView.start(party);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return minutes + " min " + secs + " sec";
    }

    private void showExitDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thoát game?")
                .setMessage("Bạn có muốn thoát game không?")
                .setPositiveButton("Thoát", (dialog, which) -> {

                    finishAffinity();
                })
                .setNegativeButton("Ở lại", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
