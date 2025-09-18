package com.example.quizgame;

public class Question {

    private String question;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String correct;

    // Constructor
    public Question(String question, String a, String b, String c, String d, String correct) {
        this.question = question;
        this.answerA = a;
        this.answerB = b;
        this.answerC = c;
        this.answerD = d;
        this.correct = correct.toUpperCase();
    }

    // Getter cho câu hỏi
    public String getQuestion() {
        return question;
    }

    // Getter cho các đáp án
    public String getAnswerA() {
        return answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    // Trả về toàn bộ nội dung của đáp án đúng
    public String getCorrect() {
        switch (correct) {
            case "A": return answerA;
            case "B": return answerB;
            case "C": return answerC;
            case "D": return answerD;
            default: return "";
        }
    }

    // Trả về ký hiệu đáp án đúng ("A", "B", "C", "D")
    public String getCorrectKey() {
        return correct;
    }
}
