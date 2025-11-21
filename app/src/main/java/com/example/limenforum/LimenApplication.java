package com.example.limenforum;

import android.app.Application;
import com.example.limenforum.data.service.ImageService;
import com.example.limenforum.data.service.LocalImageService;
import com.example.limenforum.data.service.LocalPostService;
import com.example.limenforum.data.service.PostService;

public class LimenApplication extends Application {

    private static LimenApplication instance;
    private PostService postService;
    private ImageService imageService;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Initialize Services
        postService = new LocalPostService(this);
        imageService = new LocalImageService(this);
    }

    public static LimenApplication getInstance() {
        return instance;
    }

    public PostService getPostService() {
        return postService;
    }

    public ImageService getImageService() {
        return imageService;
    }
}