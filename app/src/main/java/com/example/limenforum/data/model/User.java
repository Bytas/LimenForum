package com.example.limenforum.data.model;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class User {
    private static User instance;
    private SharedPreferences prefs;

    // Fields for persistence
    private String userId; // New ID field
    private String username;
    private String avatarUrl;
    private int postCount;
    private int likeCount;
    private boolean isLoggedIn;
    private Set<String> likedPostIds; // New field

    // Constructor for loaded users (non-singleton)
    public User(String userId, String username, String avatarUrl) {
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.postCount = 0;
        this.likeCount = 0;
        this.likedPostIds = new HashSet<>();
    }

    // Singleton constructor
    private User(Context context) {
        prefs = context.getSharedPreferences("LimenUserPrefs", Context.MODE_PRIVATE);
        this.userId = prefs.getString("userId", null);
        this.username = prefs.getString("username", "未登录");
        this.avatarUrl = prefs.getString("avatarUrl", null);
        this.postCount = prefs.getInt("postCount", 0);
        this.likeCount = prefs.getInt("likeCount", 0);
        this.isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        this.likedPostIds = new HashSet<>(prefs.getStringSet("likedPostIds", new HashSet<>()));
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
        // Generate a consistent UID for the logged in user if not exists
        if (this.userId == null) {
            this.userId = String.valueOf(Math.abs(name.hashCode())); 
        }
        if (this.avatarUrl == null) {
            this.avatarUrl = "https://testingbot.com/free-online-tools/random-avatar/200?u=" + this.userId;
        }
        save();
    }

    public void logout() {
        this.isLoggedIn = false;
        this.username = "未登录";
        this.avatarUrl = null;
        this.userId = null;
        this.likedPostIds.clear();
        save();
    }

    public void incrementPostCount() {
        this.postCount++;
        save();
    }
    
    public void updateStats(int postCount, int likeCount) {
        this.postCount = postCount;
        this.likeCount = likeCount;
        save();
    }
    
    public void addLikedPost(String postId) {
        this.likedPostIds.add(postId);
        save();
    }
    
    public void removeLikedPost(String postId) {
        this.likedPostIds.remove(postId);
        save();
    }
    
    public boolean hasLikedPost(String postId) {
        return this.likedPostIds.contains(postId);
    }

    private void save() {
        prefs.edit()
                .putString("userId", userId)
                .putString("username", username)
                .putString("avatarUrl", avatarUrl)
                .putInt("postCount", postCount)
                .putInt("likeCount", likeCount)
                .putBoolean("isLoggedIn", isLoggedIn)
                .putStringSet("likedPostIds", likedPostIds)
                .apply();
    }

    public boolean isLoggedIn() { return isLoggedIn; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getPostCount() { return postCount; }
    public int getLikeCount() { return likeCount; }
    public Set<String> getLikedPostIds() { return likedPostIds; }
    public void setLikedPostIds(Set<String> ids) { 
        this.likedPostIds = ids != null ? ids : new HashSet<>(); 
    }
}