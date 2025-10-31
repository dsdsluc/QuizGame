package com.example.quizgame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<User> userList = new ArrayList<>();

    // dùng repo để tách Firebase
    private UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LeaderboardAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        // ⬇️ load từ repo chứ không gọi Firebase trực tiếp nữa
        loadLeaderboard();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnStatistics = findViewById(R.id.btnStatistics);
        btnStatistics.setOnClickListener(v ->
                Toast.makeText(this,
                        "Chức năng thống kê sẽ được phát triển sau",
                        Toast.LENGTH_SHORT).show());
    }

    private void loadLeaderboard() {
        userRepository.getAllUsers(new UserRepository.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                userList.clear();
                userList.addAll(users);

                // sort theo score
                Collections.sort(userList, (u1, u2) ->
                        Integer.compare(
                                u2 != null ? u2.getScore() : 0,
                                u1 != null ? u1.getScore() : 0
                        )
                );

                // gán rank
                for (int i = 0; i < userList.size(); i++) {
                    userList.get(i).setRank(i + 1);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Leaderboard.this,
                        "Lấy dữ liệu thất bại: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
