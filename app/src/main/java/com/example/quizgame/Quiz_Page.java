package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class Quiz_Page extends AppCompatActivity {

    // UI
    private TextView tvQuestion, tvCorrect, tvWrong, tvScore, tvLevel, tvTime;
    private MaterialButton btnOption1, btnOption2, btnOption3, btnOption4;
    private MaterialButton btnNext, btnFinish;
    private ProgressBar progressBar;

    // Data
    private List<Question> questionList = new ArrayList<>();
    private int questionNumber = 0;

    private User currentUser;
    private String gameMode;

    // Timer
    private CountDownTimer timer;
    private long startTime;
    private final long QUESTION_TIME = 30_000; // 30s

    // Repositories
    private final QuestionRepository questionRepository = new QuestionRepository();
    private final MatchHistoryRepository matchHistoryRepository = new MatchHistoryRepository();
    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        // 1. Ánh xạ view
        tvQuestion = findViewById(R.id.tv_question);
        tvCorrect = findViewById(R.id.tv_correct);
        tvWrong = findViewById(R.id.tv_wrong);
        tvScore = findViewById(R.id.tv_score);
        tvLevel = findViewById(R.id.tv_level);
        tvTime = findViewById(R.id.tv_time);

        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);

        btnNext = findViewById(R.id.btn_next);
        btnFinish = findViewById(R.id.btn_finish);

        progressBar = findViewById(R.id.progressBar);

        startTime = System.currentTimeMillis();

        // 2. Lấy dữ liệu từ Intent + Session
        gameMode = getIntent().getStringExtra("GAME_MODE");

        currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            Toast.makeText(this, "User chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Reset quiz cho user (per-game)
        currentUser.resetQuiz(gameMode);

        // 4. Load câu hỏi từ Firebase (qua repository)
        loadQuestionsFromFirebase();

        // 5. Gán sự kiện click cho 4 option
        View.OnClickListener optionClickListener = v -> {
            int selectedIndex = 0;
            if (v == btnOption1) selectedIndex = 0;
            else if (v == btnOption2) selectedIndex = 1;
            else if (v == btnOption3) selectedIndex = 2;
            else if (v == btnOption4) selectedIndex = 3;

            checkAnswer(selectedIndex);
        };

        btnOption1.setOnClickListener(optionClickListener);
        btnOption2.setOnClickListener(optionClickListener);
        btnOption3.setOnClickListener(optionClickListener);
        btnOption4.setOnClickListener(optionClickListener);

        // 6. Nút Next
        btnNext.setOnClickListener(v -> {
            if (isLastQuestion()) {
                finishQuiz();
            } else {
                questionNumber++;
                showQuestion();
            }
        });

        // 7. Nút Finish (nếu người chơi muốn thoát sớm)
        btnFinish.setOnClickListener(v -> showExitQuizDialog());
    }

    private void loadQuestionsFromFirebase() {
        // disable tạm thời các button để user không bấm trong lúc loading
        setOptionsEnabled(false);
        btnNext.setEnabled(false);

        questionRepository.loadAllQuestions(new QuestionLoadCallback() {
            @Override
            public void onLoaded() {
                // Lấy 10 câu ngẫu nhiên đúng với mode + level
                questionList = QuestionManager.getInstance()
                        .getRandomQuestions(gameMode, currentUser.getLevel(), null, 10);

                if (questionList.isEmpty()) {
                    Toast.makeText(Quiz_Page.this, "Không có câu hỏi phù hợp", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Hiển thị câu hỏi đầu tiên
                showQuestion();
            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(Quiz_Page.this, "Lỗi tải câu hỏi: " + message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // --- Hiển thị câu hỏi ---
    private void showQuestion() {
        // Reset button options về trạng thái mặc định
        resetOptionButtons();

        // Lấy câu hỏi hiện tại
        Question q = questionList.get(questionNumber);
        tvQuestion.setText(q.getText());
        btnOption1.setText(q.getOptions().get(0));
        btnOption2.setText(q.getOptions().get(1));
        btnOption3.setText(q.getOptions().get(2));
        btnOption4.setText(q.getOptions().get(3));

        // Enable các button
        setOptionsEnabled(true);

        // Update thông tin user
        updateUI();

        // Next / Finish button
        btnNext.setEnabled(true);

        // Update ProgressBar
        updateProgressBar();

        // Hủy timer cũ nếu đang chạy
        if (timer != null) timer.cancel();

        // Khởi tạo CountDownTimer 30s
        timer = new CountDownTimer(QUESTION_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTime.setText("⏰ " + (millisUntilFinished / 1000) + "s");
                if (millisUntilFinished < 10_000) {
                    tvTime.setTextColor(getColor(R.color.red_700));
                } else {
                    tvTime.setTextColor(getColor(R.color.orange_700));
                }
            }

            @Override
            public void onFinish() {
                tvTime.setText("⏰ 0s");
                if ("Sinh tồn".equals(gameMode)) {
                    Toast.makeText(Quiz_Page.this, "Hết thời gian! Bạn thua!", Toast.LENGTH_SHORT).show();
                    endSurvivalMode();
                } else {
                    // Classic: coi như sai + khóa nút
                    currentUser.addWrongAnswer();
                    setOptionsEnabled(false);
                    highlightCorrectAnswer();
                    updateUI();
                }
            }
        }.start();
    }

    private void checkAnswer(int selectedIndex) {
        Question q = questionList.get(questionNumber);
        MaterialButton selectedButton = getOptionButton(selectedIndex);
        MaterialButton correctButton = getOptionButton(q.getCorrectIndex());

        // Dừng timer
        if (timer != null) timer.cancel();

        // Disable tất cả button
        setOptionsEnabled(false);

        if (selectedIndex == q.getCorrectIndex()) {
            currentUser.addCorrectAnswer(q.getPoints());
            selectedButton.setBackgroundTintList(getColorStateList(R.color.green_700));
        } else {
            currentUser.addWrongAnswer();
            selectedButton.setBackgroundTintList(getColorStateList(R.color.red_700));
            correctButton.setBackgroundTintList(getColorStateList(R.color.green_700));

            // Nếu đang chơi Sinh tồn, sai 1 câu → thua
            if ("Sinh tồn".equals(gameMode)) {
                Toast.makeText(this, "Sai 1 câu! Bạn thua!", Toast.LENGTH_SHORT).show();
                endSurvivalMode();
                return;
            }
        }

        // Update UI
        updateUI();
    }

    private void updateUI() {
        tvCorrect.setText("Đúng: " + currentUser.getGameCorrect());
        tvWrong.setText("Sai: " + currentUser.getGameWrong());
        tvScore.setText("Điểm: " + currentUser.getGameScore());
        tvLevel.setText("Level: " + currentUser.getLevel());
    }

    private void highlightCorrectAnswer() {
        Question q = questionList.get(questionNumber);
        MaterialButton correctButton = getOptionButton(q.getCorrectIndex());
        correctButton.setBackgroundTintList(getColorStateList(R.color.green_700));
    }

    // --- Kết thúc quiz ---
    private void finishQuiz() {
        // 1. Lưu user (điểm tích lũy, level...) lên Firebase
        userRepository.saveUser(currentUser, new UserRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                // Cập nhật thành công, có thể log hoặc hiển thị
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Quiz_Page.this, "Lỗi lưu user: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Lưu lịch sử trận đấu qua repository
        saveMatchHistoryToRepo();

        // 3. Tính thời gian chơi để gửi sang Result
        long endTime = System.currentTimeMillis();
        int totalTimePlayed = (int) ((endTime - startTime) / 1000);

        Toast.makeText(this, "Quiz kết thúc! Điểm: " + currentUser.getScore(), Toast.LENGTH_LONG).show();

        // 4. Chuyển sang màn hình kết quả
        Intent intent = new Intent(Quiz_Page.this, ResultPage.class);
        intent.putExtra("score",     currentUser.getGameScore());
        intent.putExtra("correct",   currentUser.getGameCorrect());
        intent.putExtra("wrong",     currentUser.getGameWrong());
        intent.putExtra("totalTime", totalTimePlayed);
        intent.putExtra("myRank",    currentUser.getRank());
        intent.putExtra("totalQuestions", currentUser.getGameTotalQuestions());
        startActivity(intent);
        finish();
    }

    private void saveMatchHistoryToRepo() {
        long endTime = System.currentTimeMillis();
        long timeTakenSeconds = (endTime - startTime) / 1000;

        String topic = (questionList.isEmpty() || questionList.get(0).getTopic() == null)
                ? "unknown" : questionList.get(0).getTopic();

        MatchHistory history = new MatchHistory(
                null,                               // matchId: để repo set
                currentUser.getUid(),
                currentUser.getFullName(),
                topic,
                gameMode,
                currentUser.getLevel(),
                String.valueOf(startTime),
                String.valueOf(endTime),
                currentUser.getGameTotalQuestions(),
                currentUser.getGameCorrect(),
                currentUser.getGameWrong(),
                currentUser.getGameScore(),
                currentUser.getGameAccuracy(),
                (int) timeTakenSeconds
        );

        matchHistoryRepository.saveMatchHistory(history, new MatchHistoryRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                 Toast.makeText(Quiz_Page.this, "Đã lưu lịch sử trận đấu!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Quiz_Page.this, "Lỗi lưu lịch sử: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void endSurvivalMode() {
        Intent intent = new Intent(Quiz_Page.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateProgressBar() {
        int total = questionList.size();
        int current = questionNumber + 1;
        progressBar.setMax(total);
        progressBar.setProgress(current);
    }

    private void resetOptionButtons() {
        setOptionsEnabled(true);
        btnOption1.setBackgroundTintList(getColorStateList(R.color.orange_500));
        btnOption2.setBackgroundTintList(getColorStateList(R.color.orange_500));
        btnOption3.setBackgroundTintList(getColorStateList(R.color.orange_500));
        btnOption4.setBackgroundTintList(getColorStateList(R.color.orange_500));
    }

    private void setOptionsEnabled(boolean enabled) {
        btnOption1.setEnabled(enabled);
        btnOption2.setEnabled(enabled);
        btnOption3.setEnabled(enabled);
        btnOption4.setEnabled(enabled);
    }

    private MaterialButton getOptionButton(int index) {
        switch (index) {
            case 0: return btnOption1;
            case 1: return btnOption2;
            case 2: return btnOption3;
            case 3: return btnOption4;
            default: return null;
        }
    }

    private boolean isLastQuestion() {
        return questionNumber == questionList.size() - 1;
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }

    private void showExitQuizDialog() {
        android.app.AlertDialog dialog;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_exit, null);
        builder.setView(view);
        dialog = builder.create();

        // Nếu muốn nền trong suốt bo góc đẹp
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View btnCancel = view.findViewById(R.id.btnCancel);
        View btnOk = view.findViewById(R.id.btnOk);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            if (timer != null) timer.cancel();
            Intent intent = new Intent(Quiz_Page.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });

        dialog.show();
    }

}
