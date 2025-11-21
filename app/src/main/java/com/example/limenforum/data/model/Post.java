package com.example.limenforum.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Post implements Serializable {
    private String id;
    private String username;
    private String title;
    private String content;
    private String tagName;
    private String timeAgo;
    private int likeCount;
    private int commentCount;
    private String imageUri;
    private List<Comment> comments;

    private boolean isLiked = false;

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public Post(String username, String title, String content, String tagName, String timeAgo, int likeCount, int commentCount) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.title = title;
        this.content = content;
        this.tagName = tagName;
        this.timeAgo = timeAgo;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.imageUri = null;
        this.comments = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; } // For deserialization if needed

    public String getUsername() { return username; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTagName() { return tagName; }
    public String getTimeAgo() { return timeAgo; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return comments.size(); }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public void setLikeCount(int count) { this.likeCount = count; }
    public void setCommentCount(int count) { this.commentCount = count; }

    public List<Comment> getComments() { return comments; }
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
}