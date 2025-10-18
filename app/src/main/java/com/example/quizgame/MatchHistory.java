package com.example.quizgame;

public class MatchHistory {

    private String matchId;
    private String userId;
    private String userName;
    private String quizTopic;
    private String gameMode;
    private int level;
    private String startTime;
    private String endTime;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private int score;
    private float accuracy;
    private int timeTakenSeconds;

    public MatchHistory() {
    }

    public MatchHistory(String matchId, String userId, String userName, String quizTopic,
                        String gameMode, int level, String startTime, String endTime,
                        int totalQuestions, int correctAnswers, int wrongAnswers,
                        int score, float accuracy, int timeTakenSeconds) {
        this.matchId = matchId;
        this.userId = userId;
        this.userName = userName;
        this.quizTopic = quizTopic;
        this.gameMode = gameMode;
        this.level = level;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.score = score;
        this.accuracy = accuracy;
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public String getMatchId() { return matchId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getQuizTopic() { return quizTopic; }
    public String getGameMode() { return gameMode; }
    public int getLevel() { return level; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getWrongAnswers() { return wrongAnswers; }
    public int getScore() { return score; }
    public float getAccuracy() { return accuracy; }
    public int getTimeTakenSeconds() { return timeTakenSeconds; }

    // ✅ Thêm 3 hàm tương thích với adapter:
    public int getCorrectCount() { return correctAnswers; }
    public int getWrongCount() { return wrongAnswers; }
    public int getDuration() { return timeTakenSeconds; }
}
