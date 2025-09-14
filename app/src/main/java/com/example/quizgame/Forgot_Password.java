package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password extends AppCompatActivity {

    private TextInputEditText etEmail;
    private MaterialButton btnSendLink, btnBackToLogin;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // EdgeToEdge
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();

        // Lấy reference các view
        etEmail = findViewById(R.id.etEmail);
        btnSendLink = findViewById(R.id.btnSendLink);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        progressBar = findViewById(R.id.progressBar);

        // Xử lý gửi link khôi phục
        btnSendLink.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if(email.isEmpty()){
                etEmail.setError("Vui lòng nhập email");
                etEmail.requestFocus();
                return;
            }

            // Hiện ProgressBar
            progressBar.setVisibility(View.VISIBLE);

            // Gửi email reset
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                // Ẩn ProgressBar
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){
                    Toast.makeText(Forgot_Password.this,
                            "Link khôi phục đã được gửi!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Forgot_Password.this,
                            "Gửi link thất bại: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        // Xử lý quay về trang đăng nhập
        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Forgot_Password.this, LoginPage.class);
            startActivity(intent);
            finish(); // Kết thúc Forgot_Password Activity
        });
    }
}
