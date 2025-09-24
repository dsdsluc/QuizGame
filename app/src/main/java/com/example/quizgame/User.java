package com.example.quizgame;

public class User {
    private String uid;
    private String fullName;
    private String email;

    // Thông tin cơ bản
    private int score;          // Tổng điểm
    private int level;          // Level hiện tại
    private int rank;           // Hạng trên bảng xếp hạng
    private int correct;        // Tổng số câu trả lời đúng (cả các lần chơi)
    private int wrong;          // Tổng số câu trả lời sai (cả các lần chơi)
    private int totalQuestions; // Tổng số câu hỏi đã chơi
    private int currentStreak;  // Streak hiện tại
    private int maxStreak;      // Streak dài nhất

    // Thông tin chế độ chơi
    private String gameMode;    // Classic / Survival / TimeAttack

    // Firebase yêu cầu constructor mặc định
    public User() {}

    public User(String uid, String fullName, String email,
                int score, int level, int rank,
                int correct, int wrong, int totalQuestions,
                int currentStreak, int maxStreak, String gameMode) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.score = score;
        this.level = level;
        this.rank = rank;
        this.correct = correct;
        this.wrong = wrong;
        this.totalQuestions = totalQuestions;
        this.currentStreak = currentStreak;
        this.maxStreak = maxStreak;
        this.gameMode = gameMode;
    }

    // Getter & Setter
    public String getUid() { return uid; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getRank() { return rank; }
    public int getCorrect() { return correct; }
    public int getWrong() { return wrong; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getCurrentStreak() { return currentStreak; }
    public int getMaxStreak() { return maxStreak; }
    public String getGameMode() { return gameMode; }

    public void setUid(String uid) { this.uid = uid; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setScore(int score) { this.score = score; }
    public void setLevel(int level) { this.level = level; }
    public void setRank(int rank) { this.rank = rank; }
    public void setCorrect(int correct) { this.correct = correct; }
    public void setWrong(int wrong) { this.wrong = wrong; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }

    // --- Hỗ trợ Quiz Logic ---
    public void addCorrectAnswer(int points) {
        this.correct += 1;
        this.totalQuestions += 1;
        this.currentStreak += 1;
        if (currentStreak > maxStreak) maxStreak = currentStreak;
        this.score += points + getStreakBonus();
        checkLevelUp();
    }

    public void addWrongAnswer() {
        this.wrong += 1;
        this.totalQuestions += 1;
        this.currentStreak = 0; // reset streak
        checkLevelUp();
    }

    private int getStreakBonus() {
        return currentStreak >= 3 ? currentStreak / 3 : 0;
    }

    private void checkLevelUp() {
        int newLevel = score / 100 + 1;
        if (newLevel > level) level = newLevel;
    }

    public void resetQuiz(String mode) {
        this.gameMode = mode;
        this.currentStreak = 0;
    }

    // Tỷ lệ trả lời đúng trung bình tất cả lần chơi
    public float getAccuracy() {
        if (totalQuestions == 0) return 0f;
        return (correct * 100f) / totalQuestions;
    }
}
