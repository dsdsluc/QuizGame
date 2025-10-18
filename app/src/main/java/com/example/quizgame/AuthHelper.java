package com.example.quizgame;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import com.example.quizgame.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthHelper {

    private final FirebaseAuth auth;
    private final DatabaseReference dbRef;

    public AuthHelper() {
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // ================== LOGIN ==================
    public void login(Context context, String email, String password, UserCallback callback) {
        if (!validateInput(context, email, password)) {
            callback.onFailure("Dữ liệu không hợp lệ");
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            dbRef.child(firebaseUser.getUid()).get()
                                    .addOnSuccessListener(snapshot -> {
                                        if (snapshot.exists()) {
                                            User user = snapshot.getValue(User.class);
                                            if (user != null) {
                                                UserSession.getInstance().setUser(user);
                                                callback.onSuccess(user);
                                            } else {
                                                callback.onFailure("Không tìm thấy dữ liệu User");
                                            }
                                        } else {
                                            callback.onFailure("User chưa có dữ liệu trong DB");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        callback.onFailure("Không thể tải dữ liệu từ DB");
                                    });
                        }
                    } else {
                        String errorMsg = getFirebaseErrorMessage(task.getException());
                        callback.onFailure(errorMsg);
                    }
                });
    }



    // ================== REGISTER ==================
    public void register(String fullName, String email, String password, String confirmPassword, UserCallback callback) {
        // --- Validate dữ liệu ---
        if (fullName == null || fullName.trim().isEmpty()) {
            callback.onFailure("Vui lòng nhập họ tên");
            return;
        }
        if (email == null || email.trim().isEmpty() ||
                password == null || password.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty()) {
            callback.onFailure("Vui lòng nhập đầy đủ thông tin");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            callback.onFailure("Email không hợp lệ");
            return;
        }
        if (!password.equals(confirmPassword)) {
            callback.onFailure("Mật khẩu không khớp");
            return;
        }
        if (password.length() < 6) {
            callback.onFailure("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        // --- Tạo tài khoản Firebase ---
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        String errorMsg = getFirebaseErrorMessage(task.getException());
                        callback.onFailure(errorMsg);
                        return;
                    }

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser == null) {
                        callback.onFailure("Không lấy được thông tin người dùng từ Firebase");
                        return;
                    }

                    String uid = firebaseUser.getUid();

                    // --- Tạo User object mặc định ---
                    User user = new User(
                            uid,
                            fullName,
                            email,
                            0,          // score mặc định
                            1,          // level mặc định
                            0,          // rank mặc định
                            0,          // correct
                            0,          // wrong
                            0,          // totalQuestions
                            0,          // currentStreak
                            0,          // maxStreak
                            "Classic"   // gameMode mặc định
                    );

                    // --- Lưu vào Realtime Database ---
                    dbRef.child(uid).setValue(user)
                            .addOnCompleteListener(saveTask -> {
                                if (saveTask.isSuccessful()) {
                                    // Cache user
                                    UserSession.getInstance().setUser(user);
                                    callback.onSuccess(user);
                                } else {
                                    callback.onFailure("Không thể lưu dữ liệu người dùng");
                                }
                            });
                });
    }



    // ================== LOGOUT ==================
    public void logout() {
        auth.signOut();
    }

    // ================== HELPER ==================
    private boolean validateInput(Context context, String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getFirebaseErrorMessage(Exception e) {
        if (e == null) return "Lỗi không xác định";
        String msg = e.getMessage();
        if (msg == null) return "Lỗi không xác định";
        if (msg.contains("password")) return "Sai mật khẩu";
        if (msg.contains("email")) return "Email không hợp lệ";
        if (msg.contains("no user record")) return "Tài khoản không tồn tại";
        return msg;
    }

    // ================== CALLBACK ==================
    // Callback mới
    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }
    // Thêm trong AuthHelper
    public void loadUserByUid(String uid, UserCallback callback) {
        dbRef.child(uid).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure("Không tìm thấy dữ liệu User");
                        }
                    } else {
                        callback.onFailure("User chưa có dữ liệu trong DB");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Lỗi tải dữ liệu: " + e.getMessage()));
    }
}
