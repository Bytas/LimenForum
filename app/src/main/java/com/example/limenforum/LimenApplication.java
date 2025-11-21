package com.example.limenforum;

import android.app.Application;
import com.example.limenforum.data.service.LocalPostService;
import com.example.limenforum.data.service.PostService;

public class LimenApplication extends Application {

    private static LimenApplication instance;
    private PostService postService;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Initialize LocalPostService which handles file persistence
        postService = new LocalPostService(this);
    }

    public static LimenApplication getInstance() {
        return instance;
    }

    public PostService getPostService() {
        return postService;
    }
}
