package com.example.limenforum.data.model;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    private static User instance;
    private SharedPreferences prefs;

    private String username;
    private int postCount;
    private int likeCount;
    private boolean isLoggedIn;

    private User(Context context) {
        prefs = context.getSharedPreferences("LimenUserPrefs", Context.MODE_PRIVATE);
        this.username = prefs.getString("username", "未登录");
        this.postCount = prefs.getInt("postCount", 0);
        this.likeCount = prefs.getInt("likeCount", 0);
        this.isLoggedIn = prefs.getBoolean("isLoggedIn", false);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new User(context);
        }
    }

    public static User getInstance() {
        return instance;
    }

    public void login(String name) {
        this.username = name;
        this.isLoggedIn = true;
        if (this.postCount == 0) this.postCount = 5;
        if (this.likeCount == 0) this.likeCount = 108;
        save();
    }

    public void logout() {
        this.isLoggedIn = false;
        this.username = "未登录";
        save();
    }

    public void incrementPostCount() {
        this.postCount++;
        save();
    }

    private void save() {
        prefs.edit()
                .putString("username", username)
                .putInt("postCount", postCount)
                .putInt("likeCount", likeCount)
                .putBoolean("isLoggedIn", isLoggedIn)
                .apply();
    }

    public boolean isLoggedIn() { return isLoggedIn; }
    public String getUsername() { return username; }
    public int getPostCount() { return postCount; }
    public int getLikeCount() { return likeCount; }
}
