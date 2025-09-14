package com.example.quizgame;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class Sign_Up_Page extends AppCompatActivity {

    // Ánh xạ view
    private EditText inputFullName, inputEmail, inputPassword, inputConfirmPassword;
    private MaterialButton btnSignUp;
    private TextView tvBackToLogin;
    private ProgressBar progressBarCircle;

    // Firebase Auth
    private FirebaseAuth auth;

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

        // Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Xử lý nút đăng ký
        btnSignUp.setOnClickListener(v -> {
            String fullName = inputFullName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirmPassword = inputConfirmPassword.getText().toString().trim();

            // Kiểm tra dữ liệu
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(Sign_Up_Page.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(Sign_Up_Page.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hiển thị ProgressBar
            progressBarCircle.setVisibility(View.VISIBLE);
            btnSignUp.setEnabled(false);

            // Gọi Firebase tạo tài khoản
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBarCircle.setVisibility(View.INVISIBLE);
                        btnSignUp.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(Sign_Up_Page.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(Sign_Up_Page.this,
                                    "Đăng ký thất bại: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Xử lý quay về đăng nhập
        tvBackToLogin.setOnClickListener(v -> finish()); // hoặc Intent sang LoginActivity
    }
}
