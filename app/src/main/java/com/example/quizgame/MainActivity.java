package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // drawer
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    // UI chính
    private MaterialButton btnStart, btnLeaderboard, btnAchievements;
    private ImageView imgLogout, imgMenu;
    private TextView txtWelcome, txtScore, tvLevel, tvRank;
    private Spinner spinnerGameMode;
    private ProgressBar progressLoading;

    private AuthHelper authHelper;

    // giá trị chọn
    private String selectedMode = "Classic";   // từ spinner
    private String selectedTopic = null;       // từ drawer (science / music / history ...)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   // layout mới có DrawerLayout

        // 1. Ánh xạ drawer + nav
        drawerLayout = findViewById(R.id.drawerLayout);
        navView      = findViewById(R.id.navView);

        // rất quan trọng: gán listener cho NavigationView
        navView.setNavigationItemSelectedListener(this);

        // 2. Ánh xạ view trong content
        imgMenu         = findViewById(R.id.imgMenu);
        imgLogout       = findViewById(R.id.imgLogout);
        btnStart        = findViewById(R.id.btnStart);
        btnLeaderboard  = findViewById(R.id.btnLeaderboard);
        btnAchievements = findViewById(R.id.btnAchievements);
        txtWelcome      = findViewById(R.id.txtWelcome);
        txtScore        = findViewById(R.id.tvScore);
        tvLevel         = findViewById(R.id.tvLevel);
        tvRank          = findViewById(R.id.tvRank);
        spinnerGameMode = findViewById(R.id.spinnerGameMode);
        progressLoading = findViewById(R.id.progressLoading);

        authHelper = new AuthHelper();

        // 3. Lấy user từ session
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            txtWelcome.setText("Xin chào, " + currentUser.getFullName());
            txtScore.setText("Điểm: " + currentUser.getScore());
            tvLevel.setText("Level: " + currentUser.getLevel());
            tvRank.setText("Hạng: " + currentUser.getRank());
        } else {
            // chưa login → đá về login
            startActivity(new Intent(this, LoginPage.class));
            finish();
            return;
        }

        // 4. Nút menu mở drawer
        imgMenu.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        // 5. Spinner chọn mode
        setupGameModeSpinner();


        // 7. Nút Start
        btnStart.setOnClickListener(v -> {
            User user = UserSession.getInstance().getUser();
            if (user != null) {
                user.resetQuiz(selectedMode);   // reset điểm / đúng / sai cho lần chơi này
            }

            Intent intent = new Intent(MainActivity.this, Quiz_Page.class);
            intent.putExtra("GAME_MODE", selectedMode);
            if (selectedTopic != null) {
                intent.putExtra("TOPIC", selectedTopic);
            }
            startActivity(intent);
        });

        // 8. Nút leaderboard
        btnLeaderboard.setOnClickListener(v ->
                startActivity(new Intent(this, Leaderboard.class))
        );

        // 9. Nút thành tích
        btnAchievements.setOnClickListener(v ->
                startActivity(new Intent(this, MatchHistoryActivity.class))
        );

        // 10. Logout
        imgLogout.setOnClickListener(v -> {
            authHelper.logout();
            UserSession.getInstance().clear();
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    // =========================================================
    // 1. Xử lý chọn item trong Navigation Drawer
    // =========================================================
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_topic_science) {
            selectedTopic = "science";
            Toast.makeText(this, "Chủ đề: Khoa học", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_topic_music) {
            selectedTopic = "music";
            Toast.makeText(this, "Chủ đề: Âm nhạc", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_topic_history) {
            selectedTopic = "history";
            Toast.makeText(this, "Chủ đề: Lịch sử", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_logout) {
            // hỗ trợ logout ngay trong drawer
            authHelper.logout();
            UserSession.getInstance().clear();
            startActivity(new Intent(this, LoginPage.class));
            finish();
        }

        // đóng drawer lại
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // =========================================================
    // 2. Spinner
    // =========================================================
    private void setupGameModeSpinner() {
        String[] gameModes = {"Classic", "Sinh tồn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                gameModes
        );
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
    }

    // =========================================================
    // 4. Back: nếu drawer đang mở thì đóng trước
    // =========================================================
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // vẫn để super để quay lại như cũ
            super.onBackPressed();
        }
    }
}
