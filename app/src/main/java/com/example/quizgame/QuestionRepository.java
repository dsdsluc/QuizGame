package com.example.quizgame;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query; // Thêm import này
import com.google.firebase.database.ValueEventListener;

public class QuestionRepository {

    private static final String QUESTIONS_NODE = "Questions";

    //Tải TẤT CẢ câu hỏi (Dành cho chơi ngẫu nhiên hoặc khởi tạo)
    public void loadAllQuestions(QuestionLoadCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(QUESTIONS_NODE);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // clear cũ
                QuestionManager.getInstance().clearAll();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Question q = child.getValue(Question.class);
                    if (q != null) {
                        QuestionManager.getInstance().addQuestion(q);
                    }
                }

                if (callback != null) callback.onLoaded();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                if (callback != null) callback.onFailed(error.getMessage());
            }
        });
    }

    // 🌟 HÀM MỚI: Tải câu hỏi có LỌC theo Chủ đề
    public void loadQuestionsByTopic(String topic, QuestionLoadCallback callback) {
        DatabaseReference baseRef = FirebaseDatabase.getInstance()
                .getReference(QUESTIONS_NODE);

        Query query;

        if (topic == null || topic.isEmpty()) {
            query = baseRef;
        } else {
            query = baseRef.orderByChild("topic").equalTo(topic);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // clear cũ (
                QuestionManager.getInstance().clearAll();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Question q = child.getValue(Question.class);
                    if (q != null) {
                        QuestionManager.getInstance().addQuestion(q);
                    }
                }

                if (callback != null) callback.onLoaded();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                if (callback != null) callback.onFailed(error.getMessage());
            }
        });
    }
}