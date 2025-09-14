package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lấy reference nút Start
        btnStart = findViewById(R.id.btnStart);

        // Xử lý click mở Quiz_Page
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Quiz_Page.class);
            startActivity(intent);
        });
    }
}
