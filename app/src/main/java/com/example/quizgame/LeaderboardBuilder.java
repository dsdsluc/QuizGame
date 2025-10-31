package com.example.quizgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardBuilder {

    public static List<LeaderboardItem> buildFromUsers(List<User> users) {
        // sort theo score
        Collections.sort(users, (u1, u2) ->
                Integer.compare(
                        u2 != null ? u2.getScore() : 0,
                        u1 != null ? u1.getScore() : 0
                )
        );

        List<LeaderboardItem> result = new ArrayList<>();
        int rank = 1;
        for (User u : users) {
            if (u == null) continue;
            result.add(new LeaderboardItem(
                    u.getUid(),
                    u.getFullName(),
                    u.getScore(),
                    rank
            ));
            rank++;
        }
        return result;
    }
}
