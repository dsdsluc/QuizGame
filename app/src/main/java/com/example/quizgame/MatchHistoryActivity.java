package com.example.quizgame;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
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

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setTitle("Lịch sử trận");

        topAppBar.setNavigationOnClickListener(v -> finish());


        loadHistory();
    }

    private void loadHistory() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Chỉ lấy lịch sử của user hiện tại
        Query q = historyRef.orderByChild("userId").equalTo(uid).limitToLast(50);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    MatchHistory match = data.getValue(MatchHistory.class);
                    if (match != null) historyList.add(match);
                }

                // endTime/startTime đang là String -> ép long để sort mới nhất lên đầu
                Collections.sort(historyList, (a, b) -> {
                    long ea = safeParseLong(a.getEndTime());
                    long eb = safeParseLong(b.getEndTime());
                    return Long.compare(eb, ea); // desc
                });

                // cắt top 10 nếu muốn
                if (historyList.size() > 10) {
                    historyList = new ArrayList<>(historyList.subList(0, 10));
                }

                adapter = new MatchHistoryAdapter(historyList);
                recyclerHistory.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private long safeParseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    }

}
