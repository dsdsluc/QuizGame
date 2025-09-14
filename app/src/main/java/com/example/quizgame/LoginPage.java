package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private ImageView imageViewLogo;
    private EditText inputEmail, inputPassword;
    private MaterialButton btnLogin, btnGoogleLogin;
    private TextView tvForgotPassword, tvRegister;
    private ProgressBar progressBarLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // --- Ánh xạ view ---
        imageViewLogo = findViewById(R.id.imageView);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        auth = FirebaseAuth.getInstance();

        // --- Nút đăng nhập ---
        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBarLogin.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBarLogin.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);

                        if(task.isSuccessful()){
                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginPage.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Đăng nhập thất bại: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // --- Nút Google Sign-In ---
        btnGoogleLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Đăng nhập bằng Google", Toast.LENGTH_SHORT).show();
            // TODO: Thêm Google Sign-In
        });

        // --- Quên mật khẩu ---
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, Forgot_Password.class);
            startActivity(intent);
        });

        // --- Chuyển sang trang đăng ký ---
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, Sign_Up_Page.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            // User đã đăng nhập → chuyển thẳng MainActivity
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
