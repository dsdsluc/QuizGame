package com.example.quizgame;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText editName, editEmail, editPassword;
    private EditText editBirthday;
    private Spinner spinnerGender;
    private Button btnSave;

    private UserRepository userRepo;
    private User currentUser;

    private String currentUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Ánh xạ View
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editBirthday = findViewById(R.id.edit_birthday);
        spinnerGender = findViewById(R.id.spinner_gender);
        btnSave = findViewById(R.id.btn_save);

        userRepo = new UserRepository();

        // Spinner giới tính
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Male", "Female", "Other"});
        spinnerGender.setAdapter(genderAdapter);

        // UID người dùng hiện tại
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            currentUid = "test_user_uid"; // fallback nếu test local
        }

        // Load dữ liệu user
        loadUserFromRepo(currentUid);

        // Chọn ngày sinh
        editBirthday.setOnClickListener(v -> showDatePickerDialog());

        // Nút Lưu
        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String date = String.format("%02d/%02d/%04d", day, month + 1, year);
                    editBirthday.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    // 🔹 Load user qua Repository (dựa trên getAllUsers)
    private void loadUserFromRepo(String uid) {
        userRepo.getAllUsers(new UserRepository.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                for (User u : users) {
                    if (u.getUid() != null && u.getUid().equals(uid)) {
                        currentUser = u;
                        runOnUiThread(() -> populateFields(u));
                        break;
                    }
                }

                if (currentUser == null) {
                    runOnUiThread(() -> Toast.makeText(EditProfileActivity.this,
                            "User not found!", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(EditProfileActivity.this,
                        "Load error: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    // 🔹 Fill dữ liệu vào EditText + Spinner
    private void populateFields(User user) {
        editName.setText(user.getFullName());
        editEmail.setText(user.getEmail());
        editBirthday.setText(user.getBirthday());

        if (user.getGender() != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerGender.getAdapter();
            int pos = adapter.getPosition(user.getGender());
            spinnerGender.setSelection(pos >= 0 ? pos : 0);
        }
    }

    // 🔹 Lưu lại dữ liệu qua Repository
    private void saveUserData() {
        if (currentUser == null) {
            Toast.makeText(this, "No user loaded!", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setFullName(editName.getText().toString().trim());
        currentUser.setEmail(editEmail.getText().toString().trim());
        currentUser.setGender(spinnerGender.getSelectedItem().toString());
        currentUser.setBirthday(editBirthday.getText().toString().trim());

        userRepo.updateUser(currentUser, new UserRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditProfileActivity.this,
                        "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(EditProfileActivity.this,
                        "Update failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
