package com.example.limenforum.data.service;

import com.example.limenforum.data.model.Comment;
import com.example.limenforum.data.model.Post;
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

    void getPosts(PostCallback callback);
    void getUserPosts(String username, PostCallback callback);
    
    void createPost(Post post, VoidCallback callback);
    void toggleLike(String postId, boolean isLiked, VoidCallback callback);
    void addComment(String postId, Comment comment, VoidCallback callback);
}