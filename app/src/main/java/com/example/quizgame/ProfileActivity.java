package com.example.quizgame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvScore, tvLevel, tvRank,
            tvCorrect, tvWrong, tvAccuracy, tvCurrentStreak, tvMaxStreak;
    private ImageButton btnEdit;
    private LineChart lineChart;

    private User currentUser;
    private String currentUid;
    private final MatchHistoryRepository historyRepo = new MatchHistoryRepository();
    private LinearLayout btnAchievements , btnHome, btnHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // === Ánh xạ View ===
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.tvLevel);
        tvRank = findViewById(R.id.tvRank);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvWrong = findViewById(R.id.tvWrong);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        tvCurrentStreak = findViewById(R.id.tvCurrentStreak);
        tvMaxStreak = findViewById(R.id.tvMaxStreak);
        btnEdit = findViewById(R.id.btnEdit);
        lineChart = findViewById(R.id.lineChart);
        btnAchievements = findViewById(R.id.btnAchievements);
        btnHome = findViewById(R.id.btnHome);
        btnHistory = findViewById(R.id.btnHistory);


        // === Lấy user hiện tại ===
        UserSession session = UserSession.getInstance();
        currentUser = session.getUser();

        if (currentUser != null) {
            currentUid = currentUser.getUid();
            populateUI(currentUser);
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadUserFromFirebase(currentUid);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
        }

        btnAchievements.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Leaderboard.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MatchHistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        // Nút chỉnh sửa
        btnEdit.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

    }

    // === Lấy dữ liệu user từ Firebase ===
    private void loadUserFromFirebase(String uid) {
        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            currentUser = user;
                            UserSession.getInstance().setUser(user);
                            populateUI(user);
                        } else {
                            Toast.makeText(ProfileActivity.this, "Không có dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // === Cập nhật giao diện ===
    private void populateUI(User user) {
        if (user == null) return;

        tvFullName.setText(user.getFullName());
        tvEmail.setText(user.getEmail());
        tvScore.setText(String.valueOf(user.getScore()));
        tvLevel.setText(String.valueOf(user.getLevel()));
        tvRank.setText(String.valueOf(user.getRank()));
        tvCorrect.setText(String.valueOf(user.getCorrect()));
        tvWrong.setText(String.valueOf(user.getWrong()));
        tvCurrentStreak.setText(String.valueOf(user.getCurrentStreak()));
        tvMaxStreak.setText(String.valueOf(user.getMaxStreak()));
        tvAccuracy.setText(String.format("%.1f%%", user.getAccuracy()));

        // Gọi hàm vẽ biểu đồ (dữ liệu thật)
        setupLineChart(user);
    }


    // === Biểu đồ điểm thật từ match_history ===
    private void setupLineChart(User user) {
        if (lineChart == null || user == null) return;

        // Cấu hình cơ bản
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.WHITE);

        // Lấy dữ liệu từ Firebase
        historyRepo.getUserHistory(user.getUid(), new MatchHistoryRepository.HistoryListCallback() {
            @Override
            public void onSuccess(List<MatchHistory> historyList) {
                if (historyList.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Chưa có dữ liệu lịch sử để hiển thị", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sắp xếp theo thời gian tăng dần
                Collections.sort(historyList, (a, b) -> a.getStartTime().compareTo(b.getStartTime()));

                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                int index = 0;
                for (MatchHistory match : historyList) {
                    try {
                        // Lấy thời gian thật
                        long timestamp = Long.parseLong(match.getStartTime());
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                        String label = sdf.format(new java.util.Date(timestamp));
                        labels.add(label);

                        // Lấy điểm để vẽ
                        entries.add(new Entry(index, match.getScore()));
                    } catch (NumberFormatException e) {
                        labels.add("N/A");
                        entries.add(new Entry(index, 0)); // để không làm hỏng thứ tự
                    }
                    index++;
                }

                LineDataSet dataSet = new LineDataSet(entries, "");
                dataSet.setColor(Color.parseColor("#8C5CF6"));
                dataSet.setCircleColor(Color.parseColor("#8C5CF6"));
                dataSet.setLineWidth(2f);
                dataSet.setCircleRadius(4f);
                dataSet.setValueTextSize(9f);
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                dataSet.setDrawFilled(true);
                dataSet.setFillColor(Color.parseColor("#D1C4E9"));

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);

                // Trục X
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setGranularity(1f);
                xAxis.setLabelRotationAngle(-30);
                xAxis.setTextColor(Color.DKGRAY);

                // Trục Y
                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setTextColor(Color.DKGRAY);
                leftAxis.enableGridDashedLine(10f, 10f, 0f);
                leftAxis.setDrawGridLines(true);

                lineChart.getAxisRight().setEnabled(false);

                // Chú thích
                Legend legend = lineChart.getLegend();
                legend.setTextColor(Color.DKGRAY);
                legend.setForm(Legend.LegendForm.LINE);

                lineChart.animateX(1200);
                lineChart.invalidate();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProfileActivity.this, "Lỗi tải lịch sử: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
