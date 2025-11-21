package com.example.limenforum.data.service;

import android.net.Uri;

public interface ImageService {
    interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(String errorMessage);
    }

    /**
     * Saves an image from a source Uri to internal storage.
     * @param sourceUri Content Uri of the image to upload
     * @param callback Returns the persistent file URL (file://...)
     */
    void uploadImage(Uri sourceUri, UploadCallback callback);
}
