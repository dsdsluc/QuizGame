package com.example.quizgame;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MatchHistoryRepository {

    private final DatabaseReference historyRef =
            FirebaseDatabase.getInstance().getReference("match_history");

    public interface SaveCallback {
        void onSuccess();
        void onError(String error);
    }

    public void saveMatchHistory(@NonNull MatchHistory history, SaveCallback callback) {
        // tạo key mới
        String matchId = historyRef.push().getKey();
        if (matchId == null) {
            if (callback != null) callback.onError("Không tạo được matchId");
            return;
        }

        // đảm bảo object có id
        history.setMatchId(matchId);

        historyRef.child(matchId).setValue(history)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
    }
}

