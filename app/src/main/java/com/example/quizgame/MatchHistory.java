package com.example.quizgame;

public class MatchHistory {

    private String matchId;          // ID duy nhất trên Firebase
    private String userId;           // uid của người chơi
    private String userName;         // tên người chơi lúc đó
    private String topic;            // chủ đề câu hỏi
    private String gameMode;         // Classic / Sinh tồn / TimeAttack...
    private int level;               // level của user lúc chơi

    private String startTime;        // lưu dạng String để dễ push Firebase
    private String endTime;

    private int totalQuestions;      // tổng số câu
    private int correct;             // số đúng
    private int wrong;               // số sai
    private int score;               // điểm của trận này
    private float accuracy;          // % đúng
    private int durationSeconds;     // thời gian chơi (giây)

    // --- BẮT BUỘC cho Firebase ---
    public MatchHistory() {
    }

    // --- Constructor đầy đủ (bạn đang dùng kiểu này trong Quiz_Page) ---
    public MatchHistory(String matchId,
                        String userId,
                        String userName,
                        String topic,
                        String gameMode,
                        int level,
                        String startTime,
                        String endTime,
                        int totalQuestions,
                        int correct,
                        int wrong,
                        int score,
                        float accuracy,
                        int durationSeconds) {
        this.matchId = matchId;
        this.userId = userId;
        this.userName = userName;
        this.topic = topic;
        this.gameMode = gameMode;
        this.level = level;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalQuestions = totalQuestions;
        this.correct = correct;
        this.wrong = wrong;
        this.score = score;
        this.accuracy = accuracy;
        this.durationSeconds = durationSeconds;
    }

    // --- Getter & Setter ---

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
