package com.example.limenforum.data.service;

import com.example.limenforum.data.model.Comment;
import com.example.limenforum.data.model.Post;
import com.example.limenforum.data.model.User; // Add User import
import java.util.List;

public interface PostService {
    interface PostCallback {
        void onSuccess(List<Post> posts);
        void onFailure(String errorMessage);
    }

    interface VoidCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    
    // New callback for User lookup
    interface UserCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    void getPosts(PostCallback callback);
    void getUserPosts(String userId, PostCallback callback); // Changed from username to userId
    
    // New methods for user resolution
    void getUser(String userId, UserCallback callback);
    
    void createPost(Post post, VoidCallback callback);
    void toggleLike(String postId, boolean isLiked, VoidCallback callback);
    void addComment(String postId, Comment comment, VoidCallback callback);
}