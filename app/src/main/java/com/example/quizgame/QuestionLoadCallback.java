package com.example.quizgame;

public interface QuestionLoadCallback {
    void onLoaded();               // Gọi khi tải câu hỏi xong
    void onFailed(String message); // Gọi khi lỗi (mất mạng, sai quyền, không có data...)
}
