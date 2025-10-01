package com.example.quizgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.UserViewHolder> {

    private final Context context;
    private List<User> userList;

    public LeaderboardAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    // Phân biệt layout theo thứ hạng
    @Override
    public int getItemViewType(int position) {
        int rank = userList.get(position).getRank();
        if (rank == 1) {
            return 0; // layout cho người đứng đầu
        } else if (rank >= 2 && rank <= 4) {
            return 1; // layout cho top 2-4
        } else {
            return 2; // layout cho các hạng còn lại
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        switch (viewType) {
            case 0:
                layoutRes = R.layout.item_leaderboard_top; // layout mới cho top 1
                break;
            case 1:
                layoutRes = R.layout.item_leaderboard_green;  // top 2–4
                break;
            default:
                layoutRes = R.layout.item_leaderboard_blue;   // top >= 5
                break;
        }

        View view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        holder.tvName.setText(user.getFullName() != null ? user.getFullName() : "Unknown");
        holder.tvScore.setText(String.format("%,d", user.getScore()));
        holder.tvRank.setText("#" + user.getRank());

        holder.imgAvatar.setImageResource(R.drawable.ic_user); // bạn có thể đổi sang Glide/Picasso nếu load avatar từ URL
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvScore, tvRank;
        ImageView imgAvatar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvRank = itemView.findViewById(R.id.tvRank);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
