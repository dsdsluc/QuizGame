package com.example.quizgame;

import java.util.List;

public class Question {
    private String id;               // ID duy nhất của câu hỏi
    private String text;             // Nội dung câu hỏi
    private List<String> options;    // Danh sách các lựa chọn
    private int correctIndex;        // Vị trí của đáp án đúng (0-3)
    private String topic;            // Chủ đề
    private String gameMode;         // Chế độ chơi (Classic, Sinh tồn, Đua thời gian...)
    private int level;               // Level phù hợp
    private int points;              // Điểm dành cho câu hỏi

    // Bắt buộc cho Firebase
    public Question() {}

    public Question(String id, String text, List<String> options, int correctIndex,
                    String topic, String gameMode, int level, int points) {
        this.id = id;
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
        this.topic = topic;
        this.gameMode = gameMode;
        this.level = level;
        this.points = points;
    }

    // Getter
    public String getId() { return id; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public String getTopic() { return topic; }
    public String getGameMode() { return gameMode; }
    public int getLevel() { return level; }
    public int getPoints() { return points; }

    // Setter
    public void setId(String id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setOptions(List<String> options) { this.options = options; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }
    public void setLevel(int level) { this.level = level; }
    public void setPoints(int points) { this.points = points; }
}
