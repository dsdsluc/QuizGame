package com.example.quizgame;

public interface QuestionLoadCallback {
    void onLoaded();
    void onFailed(String message);
}
