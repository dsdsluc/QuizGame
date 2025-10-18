package com.example.quizgame;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerHistory;
    private MatchHistoryAdapter adapter;
    private List<MatchHistory> historyList = new ArrayList<>();

    private DatabaseReference historyRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);

        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MatchHistoryAdapter(historyList);
        recyclerHistory.setAdapter(adapter);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        historyRef = FirebaseDatabase.getInstance().getReference("match_history");


        loadHistory();
    }

    private void loadHistory() {
        // Lấy tối đa 10 trận mới nhất
        historyRef.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    MatchHistory match = data.getValue(MatchHistory.class);
                    if (match != null) {
                        historyList.add(match);
                    }
                }

                // Đảo ngược danh sách (để trận mới nhất lên đầu)
                Collections.reverse(historyList);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
