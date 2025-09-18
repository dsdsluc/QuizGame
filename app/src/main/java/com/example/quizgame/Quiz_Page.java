package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Quiz_Page extends AppCompatActivity {

    private TextView tvQuestion, tvTime, tvCorrect, tvWrong;
    private MaterialButton btnOption1, btnOption2, btnOption3, btnOption4;
    private Button btnNext, btnFinish;
    private ProgressBar progressBar;

    private FirebaseDatabase database;
    private DatabaseReference questionsRef;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReferenceSecond;

    private ArrayList<Question> questionList = new ArrayList<>();
    private int questionNumber = 0;
    private int correctCount = 0;
    private int wrongCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        // Ánh xạ view
        tvQuestion = findViewById(R.id.tv_question);
        tvTime = findViewById(R.id.tv_time);
        tvCorrect = findViewById(R.id.tv_correct);
        tvWrong = findViewById(R.id.tv_wrong);

        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);

        btnNext = findViewById(R.id.btn_next);
        btnFinish = findViewById(R.id.btn_finish);

        progressBar = findViewById(R.id.progressBar);

        database = FirebaseDatabase.getInstance();
        questionsRef = database.getReference().child("Questions");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReferenceSecond = database.getReference();


        loadQuestionsFromFirebase();

        btnNext.setOnClickListener(v -> nextQuestion());
        btnFinish.setOnClickListener(v -> finishQuiz());
    }

    private void loadQuestionsFromFirebase() {
        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String q = data.child("q").getValue(String.class);
                    String a = data.child("a").getValue(String.class);
                    String b = data.child("b").getValue(String.class);
                    String c = data.child("c").getValue(String.class);
                    String d = data.child("d").getValue(String.class);
                    String correct = data.child("answer").getValue(String.class);

                    questionList.add(new Question(q, a, b, c, d, correct));
                }
                if (!questionList.isEmpty()) {
                    showQuestion();
                } else {
                    Toast.makeText(Quiz_Page.this, "No questions found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Quiz_Page.this, "Failed to load questions!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuestion() {
        if (questionNumber < questionList.size()) {
            Question current = questionList.get(questionNumber);
            tvQuestion.setText(current.getQuestion());
            btnOption1.setText(current.getAnswerA());
            btnOption2.setText(current.getAnswerB());
            btnOption3.setText(current.getAnswerC());
            btnOption4.setText(current.getAnswerD());

            // Reset button colors
            btnOption1.setBackgroundTintList(getColorStateList(R.color.orange_500));
            btnOption2.setBackgroundTintList(getColorStateList(R.color.orange_500));
            btnOption3.setBackgroundTintList(getColorStateList(R.color.orange_500));
            btnOption4.setBackgroundTintList(getColorStateList(R.color.orange_500));

            // Cập nhật progress
            int progress = (int) (((float) (questionNumber + 1) / questionList.size()) * 100);
            progressBar.setProgress(progress);

            // Gán click listener cho từng option
            MaterialButton[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};
            for (MaterialButton btn : buttons) {
                btn.setOnClickListener(v -> checkAnswer(btn));
            }

        } else {
            finishQuiz();
        }
    }

    private void checkAnswer(MaterialButton selectedButton) {
        String selected = selectedButton.getText().toString();
        String correct = questionList.get(questionNumber).getCorrect();

        if (selected.equals(correct)) {
            correctCount++;
            selectedButton.setBackgroundTintList(getColorStateList(R.color.green_700));
        } else {
            wrongCount++;
            selectedButton.setBackgroundTintList(getColorStateList(R.color.red_700));
        }

        tvCorrect.setText("Correct: " + correctCount);
        tvWrong.setText("Wrong: " + wrongCount);

        // Disable all buttons after selection
        btnOption1.setEnabled(false);
        btnOption2.setEnabled(false);
        btnOption3.setEnabled(false);
        btnOption4.setEnabled(false);
    }

    private void nextQuestion() {
        questionNumber++;
        // Enable buttons
        btnOption1.setEnabled(true);
        btnOption2.setEnabled(true);
        btnOption3.setEnabled(true);
        btnOption4.setEnabled(true);

        showQuestion();
    }

    private void finishQuiz() {
        Intent intent = new Intent(Quiz_Page.this, ResultPage.class);

        intent.putExtra("correctCount", correctCount);
        intent.putExtra("wrongCount", wrongCount);

        sendScore(correctCount, wrongCount);

        startActivity(intent);
        finish();
    }

    public void sendScore(int userCorrect, int userWrong) {
        if (user == null) {
            Toast.makeText(Quiz_Page.this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userUID = user.getUid();

        databaseReferenceSecond.child("scores").child(userUID).child("correct").setValue(userCorrect)
                .addOnSuccessListener(aVoid -> {
                    // Khi correct được ghi xong thì ghi wrong
                    databaseReferenceSecond.child("scores").child(userUID).child("wrong").setValue(userWrong)
                            .addOnSuccessListener(aVoid2 ->
                                    Toast.makeText(Quiz_Page.this, "Scores sent", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(Quiz_Page.this, "Failed to send wrong score", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Quiz_Page.this, "Failed to send correct score", Toast.LENGTH_SHORT).show());
    }

}
