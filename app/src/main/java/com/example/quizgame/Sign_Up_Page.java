package com.example.quizgame;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Sign_Up_Page extends AppCompatActivity {

    // Ánh xạ view
    private EditText inputFullName, inputEmail, inputPassword, inputConfirmPassword;
    private MaterialButton btnSignUp;
    private TextView tvBackToLogin;
    private ProgressBar progressBarCircle;

    // AuthHelper
    private AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        // Ánh xạ view
        inputFullName = findViewById(R.id.inputFullName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        progressBarCircle = findViewById(R.id.progressBarCircle);

        // Khởi tạo AuthHelper
        authHelper = new AuthHelper();

        // Xử lý nút đăng ký
        btnSignUp.setOnClickListener(v -> {
            String fullName = inputFullName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirmPassword = inputConfirmPassword.getText().toString().trim();

            setLoading(true);

            authHelper.register(fullName, email, password, confirmPassword, new AuthHelper.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    setLoading(false);
                    Toast.makeText(Sign_Up_Page.this,
                            "Đăng ký thành công! Xin chào " + user.getFullName(),
                            Toast.LENGTH_SHORT).show();
                    finish(); // Quay về Login
                }

                @Override
                public void onFailure(String errorMessage) {
                    setLoading(false);
                    Toast.makeText(Sign_Up_Page.this,
                            "Đăng ký thất bại: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        // Xử lý quay về đăng nhập
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void setLoading(boolean loading) {
        progressBarCircle.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSignUp.setEnabled(!loading);
    }
}
