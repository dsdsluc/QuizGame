package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBarLogin;
    private TextView tvForgotPassword;
    private FloatingActionButton tvRegister;

    private AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Init views
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        // Init AuthHelper
        authHelper = new AuthHelper();

        // Xử lý login
        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            setLoading(true);

            authHelper.login(this, email, password, new AuthHelper.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    setLoading(false);
                    UserSession.getInstance().setUser(user); // lưu vào session
                    Toast.makeText(LoginPage.this,
                            "Xin chào " + user.getFullName() + "!",
                            Toast.LENGTH_SHORT).show();
                    goToMain();
                }

                @Override
                public void onFailure(String errorMessage) {
                    setLoading(false);
                    Toast.makeText(LoginPage.this,
                            "Đăng nhập thất bại: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                }
            });
        });


        // Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, Forgot_Password.class);
            startActivity(intent);
        });

        // Đăng ký
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, Sign_Up_Page.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = authHelper.getCurrentUser();
        if (firebaseUser != null) {
            // Load thông tin user từ DB bằng uid
            setLoading(true);
            String uid = firebaseUser.getUid();
            authHelper.loadUserByUid(uid, new AuthHelper.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    setLoading(false);
                    UserSession.getInstance().setUser(user);
                    goToMain();
                }

                @Override
                public void onFailure(String errorMessage) {
                    setLoading(false);
                    Toast.makeText(LoginPage.this,
                            "Không thể tải dữ liệu người dùng: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBarLogin.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);

    }
}
