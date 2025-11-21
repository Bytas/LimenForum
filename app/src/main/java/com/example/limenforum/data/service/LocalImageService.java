package com.example.limenforum.data.service;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalImageService implements ImageService {

    private static final String IMAGE_DIR = "uploaded_images";
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public LocalImageService(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void uploadImage(Uri sourceUri, UploadCallback callback) {
        executor.execute(() -> {
            try {
                // 1. Create directory if not exists
                File dir = new File(context.getFilesDir(), IMAGE_DIR);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        postFailure(callback, "Failed to create image directory");
                        return;
                    }
                }

                // 2. Generate unique filename
                String filename = "IMG_" + UUID.randomUUID().toString() + ".jpg";
                File destFile = new File(dir, filename);

                // 3. Copy content
                try (InputStream is = context.getContentResolver().openInputStream(sourceUri);
                     FileOutputStream fos = new FileOutputStream(destFile)) {
                    
                    if (is == null) {
                        postFailure(callback, "Cannot open image source");
                        return;
                    }

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }

                // 4. Return file URI
                // Using file:// URI for local file access within the app
                String resultUrl = "file://" + destFile.getAbsolutePath();
                postSuccess(callback, resultUrl);

            } catch (Exception e) {
                Log.e("LocalImageService", "Upload failed", e);
                postFailure(callback, "Image upload failed: " + e.getMessage());
            }
        });
    }

    private void postSuccess(UploadCallback callback, String url) {
        mainHandler.post(() -> callback.onSuccess(url));
    }

    private void postFailure(UploadCallback callback, String error) {
        mainHandler.post(() -> callback.onFailure(error));
    }
}
