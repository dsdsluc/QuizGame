package com.example.quizgame;

import androidx.annotation.NonNull;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRepository {

    private final DatabaseReference usersRef =
            FirebaseDatabase.getInstance().getReference("users");

    public interface UserListCallback {
        void onSuccess(List<User> users);
        void onError(String error);
    }

    // LẤY TOÀN BỘ USER (bạn đã có)
    public void getAllUsers(UserListCallback callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> list = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user != null) list.add(user);
                }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // ==========================
    // 1 hàm dành riêng cho ResultPage
    // lấy “hàng xóm” của 1 user trong BXH
    // ==========================
    public interface NeighborCallback {
        void onSuccess(List<User> neighbors);
        void onError(String error);
    }

    public void getNeighbors(String currentUid, NeighborCallback callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> allUsers = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user != null) {
                        allUsers.add(user);
                    }
                }

                // sắp xếp theo điểm giảm dần
                Collections.sort(allUsers, (u1, u2) ->
                        Integer.compare(
                                u2 != null ? u2.getScore() : 0,
                                u1 != null ? u1.getScore() : 0
                        )
                );

                // gán rank
                for (int i = 0; i < allUsers.size(); i++) {
                    allUsers.get(i).setRank(i + 1);
                }

                // tìm vị trí user hiện tại
                int myIndex = -1;
                for (int i = 0; i < allUsers.size(); i++) {
                    User u = allUsers.get(i);
                    if (u != null && u.getUid() != null && u.getUid().equals(currentUid)) {
                        myIndex = i;
                        break;
                    }
                }

                List<User> neighbors = new ArrayList<>();
                if (myIndex != -1) {
                    if (myIndex - 1 >= 0) neighbors.add(allUsers.get(myIndex - 1));
                    neighbors.add(allUsers.get(myIndex));
                    if (myIndex + 1 < allUsers.size()) neighbors.add(allUsers.get(myIndex + 1));
                }

                callback.onSuccess(neighbors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // ==========================
    // Hàm lưu user (bạn đã thêm ở trên)
    // ==========================
    public interface SaveCallback {
        void onSuccess();
        void onError(String error);
    }

    public void saveUser(@NonNull User user, SaveCallback callback) {
        if (user.getUid() == null || user.getUid().isEmpty()) {
            if (callback != null) callback.onError("UID người dùng rỗng!");
            return;
        }

        usersRef.child(user.getUid()).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
    }
}
