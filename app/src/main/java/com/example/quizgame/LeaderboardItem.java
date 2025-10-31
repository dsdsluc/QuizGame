package com.example.quizgame;

public class LeaderboardItem {
    private String userId;
    private String name;
    private int score;
    private int rank;

    public LeaderboardItem(String userId, String name, int score, int rank) {
        this.userId = userId;
        this.name = name;
        this.score = score;
        this.rank = rank;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getRank() { return rank; }
}
