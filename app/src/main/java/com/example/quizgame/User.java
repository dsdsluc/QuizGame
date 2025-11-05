package com.example.quizgame;

public class User {
    private String uid;
    private String fullName;

    private String gender;
    private String birthday;
    private String email;

    // ===== Lifetime (tích lũy) =====
    private int score;          // Tổng điểm tích lũy
    private int level;          // Level hiện tại
    private int rank;           // Hạng trên BXH
    private int correct;        // Tổng đúng (tích lũy)
    private int wrong;          // Tổng sai (tích lũy)
    private int totalQuestions; // Tổng số câu (tích lũy)
    private int currentStreak;  // Streak hiện tại (tích lũy)
    private int maxStreak;      // Streak dài nhất (tích lũy)

    // ===== Thông tin chế độ chơi =====
    private String gameMode;    // Classic / Survival / TimeAttack

    // ===== Per-game (chỉ số của TRẬN hiện tại) =====
    private int gameScore;
    private int gameCorrect;
    private int gameWrong;
    private int gameTotalQuestions;
    private int gameStreak;
    private int gameMaxStreak;

    // Firebase yêu cầu constructor mặc định
    public User() {
        // đảm bảo per-game = 0 khi Firebase khởi tạo
        resetGameOnly();
    }

    public User(String uid, String fullName, String gender, String birthday, String email) {
        this.uid = uid;
        this.fullName = fullName;
        this.gender = gender;
        this.birthday = birthday;
        this.email = email;
    }

    // GIỮ NGUYÊN constructor bạn đang dùng ở nhiều nơi (KHÔNG đổi chữ ký)
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

        // Khởi tạo per-game về 0 để không ảnh hưởng code cũ
        resetGameOnly();
    }

    // ===== Getter & Setter =====
    public String getUid() { return uid; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getRank() { return rank; }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }
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
    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public void setCorrect(int correct) { this.correct = correct; }
    public void setWrong(int wrong) { this.wrong = wrong; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }

    // ====== Getter per-game (để lưu lịch sử mỗi trận) ======
    public int getGameScore() { return gameScore; }
    public int getGameCorrect() { return gameCorrect; }
    public int getGameWrong() { return gameWrong; }
    public int getGameTotalQuestions() { return gameTotalQuestions; }
    public int getGameStreak() { return gameStreak; }
    public int getGameMaxStreak() { return gameMaxStreak; }
    public float getGameAccuracy() {
        if (gameTotalQuestions == 0) return 0f;
        return (gameCorrect * 100f) / (float) gameTotalQuestions;
    }

    // ====== Hỗ trợ Quiz Logic ======
    public void addCorrectAnswer(int points) {
        // --- Lifetime ---
        this.correct += 1;
        this.totalQuestions += 1;
        this.currentStreak += 1;
        if (currentStreak > maxStreak) maxStreak = currentStreak;
        this.score += points + getStreakBonus(this.currentStreak);

        // --- Per-game ---
        this.gameCorrect += 1;
        this.gameTotalQuestions += 1;
        this.gameStreak += 1;
        if (gameStreak > gameMaxStreak) gameMaxStreak = gameStreak;
        this.gameScore += points + getStreakBonus(this.gameStreak);

        checkLevelUp();
    }

    public void addWrongAnswer() {
        // --- Lifetime ---
        this.wrong += 1;
        this.totalQuestions += 1;
        this.currentStreak = 0;

        // --- Per-game ---
        this.gameWrong += 1;
        this.gameTotalQuestions += 1;
        this.gameStreak = 0;

        checkLevelUp();
    }

    private int getStreakBonus(int streak) {
        return streak >= 3 ? streak / 3 : 0;
    }

    private void checkLevelUp() {
        int newLevel = score / 100 + 1;
        if (newLevel > level) level = newLevel;
    }

    // Reset chỉ số TRẬN (không ảnh hưởng lifetime)
    private void resetGameOnly() {
        this.gameScore = 0;
        this.gameCorrect = 0;
        this.gameWrong = 0;
        this.gameTotalQuestions = 0;
        this.gameStreak = 0;
        this.gameMaxStreak = 0;
    }

    // Gọi khi bắt đầu ván mới
    public void resetQuiz(String mode) {
        this.gameMode = mode;
        // tuỳ ý: currentStreak (lifetime) có reset không
        this.currentStreak = 0;
        resetGameOnly();
    }

    // Accuracy tích lũy
    public float getAccuracy() {
        if (totalQuestions == 0) return 0f;
        return (correct * 100f) / (float) totalQuestions;
    }
}
