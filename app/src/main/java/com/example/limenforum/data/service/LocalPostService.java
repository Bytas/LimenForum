package com.example.limenforum.data.service;

import android.content.Context;
import android.util.Log;
import com.example.limenforum.data.model.Comment;
import com.example.limenforum.data.model.Post;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

public class LocalPostService implements PostService {

    private static final String FILENAME = "posts_data.json";
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Simple cache to avoid reading disk every time
    private List<Post> cachedPosts = null;

    public LocalPostService(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void getPosts(PostCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            mainHandler.post(() -> callback.onSuccess(new ArrayList<>(cachedPosts)));
        });
    }

    @Override
    public void getUserPosts(String username, PostCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            List<Post> userPosts = new ArrayList<>();
            for (Post p : cachedPosts) {
                if (p.getUsername().equals(username)) {
                    userPosts.add(p);
                }
            }
            mainHandler.post(() -> callback.onSuccess(userPosts));
        });
    }

    @Override
    public void createPost(Post post, VoidCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            // Add to top
            cachedPosts.add(0, post);
            saveData();
            mainHandler.post(callback::onSuccess);
        });
    }

    @Override
    public void toggleLike(String postId, boolean isLiked, VoidCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            for (Post p : cachedPosts) {
                if (p.getId().equals(postId)) {
                    p.setLiked(isLiked);
                    p.setLikeCount(isLiked ? p.getLikeCount() + 1 : Math.max(0, p.getLikeCount() - 1));
                    break;
                }
            }
            saveData();
            mainHandler.post(callback::onSuccess);
        });
    }

    @Override
    public void addComment(String postId, Comment comment, VoidCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            for (Post p : cachedPosts) {
                if (p.getId().equals(postId)) {
                    p.addComment(comment);
                    p.setCommentCount(p.getComments().size());
                    break;
                }
            }
            saveData();
            mainHandler.post(callback::onSuccess);
        });
    }

    private synchronized void ensureDataLoaded() {
        if (cachedPosts == null) {
            File file = new File(context.getFilesDir(), FILENAME);
            if (!file.exists()) {
                // Initialize with mock data if file doesn't exist
                cachedPosts = new MockPostService().getInitialMockData();
                saveData(); // Save initial data to file
            } else {
                // Read from file
                cachedPosts = readFromFile(file);
            }
        }
    }

    private void saveData() {
        if (cachedPosts == null) return;
        File file = new File(context.getFilesDir(), FILENAME);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            String jsonString = serializePosts(cachedPosts);
            fos.write(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.e("LocalPostService", "Error saving data", e);
        }
    }

    private List<Post> readFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return deserializePosts(sb.toString());
        } catch (Exception e) {
            Log.e("LocalPostService", "Error reading data", e);
            return new ArrayList<>();
        }
    }

    // Helper: Serialize List<Post> to JSON String
    private String serializePosts(List<Post> posts) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Post p : posts) {
            JSONObject obj = new JSONObject();
            obj.put("id", p.getId());
            obj.put("username", p.getUsername());
            obj.put("title", p.getTitle());
            obj.put("content", p.getContent());
            obj.put("tagName", p.getTagName());
            obj.put("timeAgo", p.getTimeAgo());
            obj.put("likeCount", p.getLikeCount());
            obj.put("commentCount", p.getCommentCount());
            obj.put("isLiked", p.isLiked());
            if (p.getImageUri() != null) obj.put("imageUri", p.getImageUri());

            // Serialize comments
            JSONArray commentsArray = new JSONArray();
            for (Comment c : p.getComments()) {
                JSONObject cObj = new JSONObject();
                cObj.put("username", c.getUsername());
                cObj.put("content", c.getContent());
                commentsArray.put(cObj);
            }
            obj.put("comments", commentsArray);

            jsonArray.put(obj);
        }
        return jsonArray.toString();
    }

    // Helper: Deserialize JSON String to List<Post>
    private List<Post> deserializePosts(String jsonString) throws JSONException {
        List<Post> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String username = obj.getString("username");
            String title = obj.has("title") ? obj.getString("title") : "";
            String content = obj.getString("content");
            String tagName = obj.getString("tagName");
            String timeAgo = obj.getString("timeAgo");
            int likeCount = obj.getInt("likeCount");
            int commentCount = obj.getInt("commentCount");

            Post post = new Post(username, title, content, tagName, timeAgo, likeCount, commentCount);
            if (obj.has("id")) post.setId(obj.getString("id"));
            if (obj.has("imageUri")) post.setImageUri(obj.getString("imageUri"));
            if (obj.has("isLiked")) post.setLiked(obj.getBoolean("isLiked"));

            if (obj.has("comments")) {
                JSONArray commentsArray = obj.getJSONArray("comments");
                for (int j = 0; j < commentsArray.length(); j++) {
                    JSONObject cObj = commentsArray.getJSONObject(j);
                    post.addComment(new Comment(cObj.getString("username"), cObj.getString("content")));
                }
            }
            list.add(post);
        }
        return list;
    }
}
