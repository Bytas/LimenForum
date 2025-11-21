package com.example.limenforum.data.service;

import com.example.limenforum.data.model.Post;
import java.util.List;

public interface PostService {
    interface PostCallback {
        void onSuccess(List<Post> posts);
        void onFailure(String errorMessage);
    }

    void getPosts(PostCallback callback);
    void getUserPosts(String username, PostCallback callback); // New method
}
