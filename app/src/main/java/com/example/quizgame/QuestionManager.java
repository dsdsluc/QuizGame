package com.example.quizgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionManager {

    // Singleton instance
    private static QuestionManager instance;

    // List chứa toàn bộ câu hỏi
    private List<Question> allQuestions;

    // Private constructor
    private QuestionManager() {
        allQuestions = new ArrayList<>();
    }

    // Lấy instance singleton
    public static QuestionManager getInstance() {
        if (instance == null) {
            instance = new QuestionManager();
        }
        return instance;
    }

    // --- Các phương thức quản lý câu hỏi ---

    // Thêm câu hỏi mới
    public void addQuestion(Question q) {
        allQuestions.add(q);
    }

    // Lấy danh sách câu hỏi theo level, gameMode, topic
    public List<Question> getQuestions(String gameMode, int level, String topic) {
        return allQuestions.stream()
                .filter(q -> q.getGameMode().equals(gameMode))
                .filter(q -> q.getLevel() <= level)
                .filter(q -> topic == null || q.getTopic().equals(topic))
                .collect(Collectors.toList());
    }


    // Lấy N câu hỏi ngẫu nhiên
    public List<Question> getRandomQuestions(String gameMode, int level, String topic, int count) {
        List<Question> filtered = getQuestions(gameMode, level, topic);
        Collections.shuffle(filtered);
        if (filtered.size() <= count) return filtered;
        return filtered.subList(0, count);
    }

    // Xóa tất cả câu hỏi
    public void clearAll() {
        allQuestions.clear();
    }

    // Lấy tổng số câu hỏi đã load
    public int getTotalQuestions() {
        return allQuestions.size();
    }
}
