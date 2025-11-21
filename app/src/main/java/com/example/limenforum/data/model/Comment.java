package com.example.limenforum.data.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String userId;
    private String username; // Keeping username for now for legacy compatibility/display fallback
    private String content;
    private String avatarUrl; // New field for display

    public Comment(String userId, String username, String content) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        // Default avatar based on userId if not provided
        this.avatarUrl = "https://testingbot.com/free-online-tools/random-avatar/200?u=" + userId;
    }
    
    // Constructor for legacy compatibility (will use hash of username as pseudo-uid)
    public Comment(String username, String content) {
        this.username = username;
        this.content = content;
        this.userId = String.valueOf(Math.abs(username.hashCode()));
        this.avatarUrl = "https://testingbot.com/free-online-tools/random-avatar/200?u=" + this.userId;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String url) { this.avatarUrl = url; }
}