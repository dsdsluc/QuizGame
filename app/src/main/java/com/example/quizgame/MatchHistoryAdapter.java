package com.example.quizgame;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchHistoryAdapter extends RecyclerView.Adapter<MatchHistoryAdapter.ViewHolder> {

    private final List<MatchHistory> historyList;

    public MatchHistoryAdapter(List<MatchHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match_history, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchHistory match = historyList.get(position);
        if (match == null) return;

        // ===== 1. Format ngày từ endTime (String millis) =====
        String dateString = "Không rõ";
        long endTimeMillis = 0L;
        try {
            String et = match.getEndTime();          // endTime bạn lưu kiểu String millis
            if (et != null && !et.isEmpty()) {
                endTimeMillis = Long.parseLong(et);
            }
        } catch (Exception ignored) { }

        if (endTimeMillis > 0) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf =
                    new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
            dateString = sdf.format(new Date(endTimeMillis));
        }

        holder.tvDate.setText("🗓 " + dateString);

        // ===== 2. Điểm =====
        holder.tvScore.setText("⭐ Điểm: " + match.getScore());

        // ===== 3. Đúng / Sai / Tổng =====
        holder.tvQuestions.setText(
                "✔ Đúng: " + match.getCorrect()
                        + " | ❌ Sai: " + match.getWrong()
                        + " | Tổng: " + match.getTotalQuestions()
        );

        // ===== 4. Thời gian chơi =====
        holder.tvTime.setText("⏱ Thời gian: " + match.getDurationSeconds() + " giây");

        // ===== 5. Chế độ chơi =====
        if (holder.tvMode != null) {
            String mode = match.getGameMode();
            holder.tvMode.setText(mode != null ? mode : "Unknown");
        }

        // ===== 6. Đổi màu card theo điểm =====
        int sc = match.getScore();
        if (sc >= 80) {
            holder.cardContainer.setCardBackgroundColor(Color.parseColor("#C8E6C9")); // xanh nhạt
        } else if (sc >= 50) {
            holder.cardContainer.setCardBackgroundColor(Color.parseColor("#FFF9C4")); // vàng nhạt
        } else {
            holder.cardContainer.setCardBackgroundColor(Color.parseColor("#FFCDD2")); // đỏ nhạt
        }
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvScore, tvQuestions, tvTime, tvMode;
        CardView cardContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvQuestions = itemView.findViewById(R.id.tvQuestions);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMode = itemView.findViewById(R.id.tvMode);              // có thể null nếu layout không có
            cardContainer = itemView.findViewById(R.id.cardContainer); // nhớ khai báo trong layout
        }
    }
}
