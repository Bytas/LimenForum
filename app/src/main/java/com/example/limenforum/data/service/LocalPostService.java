package com.example.limenforum.data.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.limenforum.data.model.Comment;
import com.example.limenforum.data.model.Post;
import com.example.limenforum.data.model.User;
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

    private static final String POSTS_FILENAME = "posts_data.json";
    private static final String USERS_FILENAME = "users_data.json";
    private static final String PREF_NAME = "LimenDataPrefs";
    private static final String KEY_DB_VERSION = "db_version";
    private static final String CURRENT_DB_VERSION = "20251121-1830"; // Bumped version

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<Post> cachedPosts = null;
    private List<User> cachedUsers = null;

    public LocalPostService(Context context) {
        this.context = context.getApplicationContext();
        checkDatabaseVersion();
    }

    private void checkDatabaseVersion() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedVersion = prefs.getString(KEY_DB_VERSION, "");

        if (!CURRENT_DB_VERSION.equals(savedVersion)) {
            Log.d("LocalPostService", "DB Version mismatch. Resetting data. Old: " + savedVersion + " New: " + CURRENT_DB_VERSION);
            File postsFile = new File(context.getFilesDir(), POSTS_FILENAME);
            if (postsFile.exists()) postsFile.delete();
            File usersFile = new File(context.getFilesDir(), USERS_FILENAME);
            if (usersFile.exists()) usersFile.delete();
            
            cachedPosts = null;
            cachedUsers = null;
            
            prefs.edit().putString(KEY_DB_VERSION, CURRENT_DB_VERSION).apply();
        }
    }

    @Override
    public void getPosts(PostCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            // Resolve user data for each post before returning
            List<Post> resolvedPosts = new ArrayList<>();
            for (Post p : cachedPosts) {
                resolvePostUser(p);
                resolvedPosts.add(p);
            }
            mainHandler.post(() -> callback.onSuccess(new ArrayList<>(resolvedPosts)));
        });
    }

    @Override
    public void getUserPosts(String userId, PostCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            List<Post> userPosts = new ArrayList<>();
            for (Post p : cachedPosts) {
                if (p.getUserId().equals(userId)) {
                    resolvePostUser(p);
                    userPosts.add(p);
                }
            }
            // Recalculate and update stats if it's the current user
            final int postCount = userPosts.size();
            int totalLikes = 0;
            for (Post p : userPosts) totalLikes += p.getLikeCount();
            final int finalTotalLikes = totalLikes;

            mainHandler.post(() -> {
                User currentUser = User.getInstance();
                if (currentUser.isLoggedIn() && currentUser.getUserId().equals(userId)) {
                    currentUser.updateStats(postCount, finalTotalLikes);
                    saveUserToInternalDb(currentUser); 
                }
                callback.onSuccess(userPosts);
            });
        });
    }
    
    @Override
    public void getLikedPosts(String userId, PostCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            List<Post> likedPosts = new ArrayList<>();
            User currentUser = User.getInstance(); // Currently only support logged in user's liked list easily
            
            if (currentUser.isLoggedIn() && currentUser.getUserId().equals(userId)) {
                for (Post p : cachedPosts) {
                    if (currentUser.hasLikedPost(p.getId())) {
                        resolvePostUser(p);
                        // IMPORTANT: Set isLiked state correctly for the viewer
                        p.setLiked(true);
                        likedPosts.add(p);
                    }
                }
            } else {
                // If we wanted to view others' likes, we'd need to find User by ID and get their liked list
                // But our simple User model serialization doesn't fully capture liked lists yet in JSON deeply enough or public access
                // For now, support current user only.
            }
            
            mainHandler.post(() -> callback.onSuccess(likedPosts));
        });
    }
    
    @Override
    public void getUser(String userId, UserCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            User user = findUserById(userId);
            mainHandler.post(() -> {
                if (user != null) {
                    callback.onSuccess(user);
                } else {
                    callback.onFailure("User not found");
                }
            });
        });
    }
    
    private void resolvePostUser(Post p) {
        User u = findUserById(p.getUserId());
        if (u != null) {
            p.setDisplayUsername(u.getUsername());
            p.setDisplayAvatarUrl(u.getAvatarUrl());
        } else {
            // Fallback
            p.setDisplayUsername("User " + p.getUserId());
            p.setDisplayAvatarUrl("https://testingbot.com/free-online-tools/random-avatar/200?u=" + p.getUserId());
        }
        
        // Check if current user liked this post (sync state)
        User currentUser = User.getInstance();
        if (currentUser.isLoggedIn()) {
            p.setLiked(currentUser.hasLikedPost(p.getId()));
        }
    }

    private User findUserById(String userId) {
        if (cachedUsers == null) return null;
        for (User u : cachedUsers) {
            if (u.getUserId().equals(userId)) return u;
        }
        return null;
    }

    @Override
    public void createPost(Post post, VoidCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            cachedPosts.add(0, post);
            savePostsData();
            // Ensure current user is in the DB too
            User currentUser = User.getInstance();
            if (currentUser.getUserId().equals(post.getUserId())) {
                saveUserToInternalDb(currentUser);
            }
            mainHandler.post(callback::onSuccess);
        });
    }

    @Override
    public void toggleLike(String postId, boolean isLiked, VoidCallback callback) {
        executor.execute(() -> {
            ensureDataLoaded();
            User currentUser = User.getInstance();
            if (!currentUser.isLoggedIn()) {
                mainHandler.post(() -> callback.onFailure("Not logged in"));
                return;
            }

            for (Post p : cachedPosts) {
                if (p.getId().equals(postId)) {
                    // Prevent double counting if state is already what we want
                    // This fixes the +2 bug where Adapter optimistic update + Service update doubled it
                    if (p.isLiked() == isLiked) {
                        // State matches, but ensure persistence of current state
                        savePostsData();
                        // Update User liked list persistence just in case
                        if (isLiked) currentUser.addLikedPost(postId);
                        else currentUser.removeLikedPost(postId);
                        
                        mainHandler.post(callback::onSuccess);
                        return;
                    }

                    // Update Post State
                    p.setLiked(isLiked);
                    p.setLikeCount(isLiked ? p.getLikeCount() + 1 : Math.max(0, p.getLikeCount() - 1));
                    
                    // Update User State
                    if (isLiked) currentUser.addLikedPost(postId);
                    else currentUser.removeLikedPost(postId);
                    
                    break;
                }
            }
            savePostsData();
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
            savePostsData();
            mainHandler.post(callback::onSuccess);
        });
    }

    // --- Data Management ---

    private synchronized void ensureDataLoaded() {
        if (cachedUsers == null) {
             File file = new File(context.getFilesDir(), USERS_FILENAME);
             if (file.exists()) {
                 cachedUsers = readUsersFromFile(file);
             } else {
                 cachedUsers = new ArrayList<>();
                 // Generate initial mock users
                 cachedUsers.addAll(new MockPostService().getInitialMockUsers());
                 saveUsersData();
             }
        }
        
        if (cachedPosts == null) {
            File file = new File(context.getFilesDir(), POSTS_FILENAME);
            if (!file.exists()) {
                cachedPosts = new MockPostService().getInitialMockData(); // Now returns posts with UIDs
                savePostsData();
            } else {
                cachedPosts = readPostsFromFile(file);
            }
        }
    }

    private void savePostsData() {
        if (cachedPosts == null) return;
        File file = new File(context.getFilesDir(), POSTS_FILENAME);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            String jsonString = serializePosts(cachedPosts);
            fos.write(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.e("LocalPostService", "Error saving post data", e);
        }
    }
    
    private void saveUsersData() {
        if (cachedUsers == null) return;
        File file = new File(context.getFilesDir(), USERS_FILENAME);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            String jsonString = serializeUsers(cachedUsers);
            fos.write(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.e("LocalPostService", "Error saving user data", e);
        }
    }
    
    private void saveUserToInternalDb(User currentUser) {
        // This method is called within executor threads usually
        boolean found = false;
        for (int i = 0; i < cachedUsers.size(); i++) {
            if (cachedUsers.get(i).getUserId().equals(currentUser.getUserId())) {
                cachedUsers.set(i, currentUser); // Update existing
                found = true;
                break;
            }
        }
        if (!found) {
            cachedUsers.add(currentUser);
        }
        saveUsersData();
    }

    // --- Serialization Helpers ---

    private List<Post> readPostsFromFile(File file) {
        String json = readFileContent(file);
        try {
            return deserializePosts(json);
        } catch (JSONException e) {
            Log.e("LocalPostService", "Error parsing posts", e);
            return new ArrayList<>();
        }
    }
    
    private List<User> readUsersFromFile(File file) {
        String json = readFileContent(file);
        try {
            return deserializeUsers(json);
        } catch (JSONException e) {
            Log.e("LocalPostService", "Error parsing users", e);
            return new ArrayList<>();
        }
    }
    
    private String readFileContent(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    // Serialize List<Post> to JSON String
    private String serializePosts(List<Post> posts) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Post p : posts) {
            JSONObject obj = new JSONObject();
            obj.put("id", p.getId());
            obj.put("userId", p.getUserId()); // Changed
            obj.put("title", p.getTitle());
            obj.put("content", p.getContent());
            obj.put("tagName", p.getTagName());
            obj.put("timeAgo", p.getTimeAgo());
            obj.put("likeCount", p.getLikeCount());
            obj.put("commentCount", p.getCommentCount());
            obj.put("isLiked", p.isLiked()); // Persist liked state in post too, though User preference overrides it for logic
            if (p.getImageUri() != null) obj.put("imageUri", p.getImageUri());

            // Serialize comments
            JSONArray commentsArray = new JSONArray();
            for (Comment c : p.getComments()) {
                JSONObject cObj = new JSONObject();
                cObj.put("userId", c.getUserId()); // ADDED
                cObj.put("username", c.getUsername());
                cObj.put("content", c.getContent());
                cObj.put("avatarUrl", c.getAvatarUrl()); // ADDED
                commentsArray.put(cObj);
            }
            obj.put("comments", commentsArray);
            jsonArray.put(obj);
        }
        return jsonArray.toString();
    }
    
    // Serialize List<User> to JSON String
    private String serializeUsers(List<User> users) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (User u : users) {
            JSONObject obj = new JSONObject();
            obj.put("userId", u.getUserId());
            obj.put("username", u.getUsername());
            if (u.getAvatarUrl() != null) obj.put("avatarUrl", u.getAvatarUrl());
            obj.put("postCount", u.getPostCount());
            obj.put("likeCount", u.getLikeCount());
            jsonArray.put(obj);
        }
        return jsonArray.toString();
    }

    // Deserialize JSON String to List<Post>
    private List<Post> deserializePosts(String jsonString) throws JSONException {
        List<Post> list = new ArrayList<>();
        if (jsonString == null || jsonString.isEmpty()) return list;
        
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            // Use userId for reconstruction
            String userId = obj.has("userId") ? obj.getString("userId") : (obj.has("username") ? String.valueOf(Math.abs(obj.getString("username").hashCode())) : "0");
            
            String title = obj.has("title") ? obj.getString("title") : "";
            String content = obj.getString("content");
            String tagName = obj.getString("tagName");
            String timeAgo = obj.getString("timeAgo");
            int likeCount = obj.getInt("likeCount");
            int commentCount = obj.getInt("commentCount");

            Post post = new Post(userId, title, content, tagName, timeAgo, likeCount, commentCount);
            if (obj.has("id")) post.setId(obj.getString("id"));
            if (obj.has("imageUri")) post.setImageUri(obj.getString("imageUri"));
            // Don't trust 'isLiked' from JSON for global posts, rely on User prefs at runtime, 
            // but keep it for consistency if needed.
            if (obj.has("isLiked")) post.setLiked(obj.getBoolean("isLiked"));

            if (obj.has("comments")) {
                JSONArray commentsArray = obj.getJSONArray("comments");
                for (int j = 0; j < commentsArray.length(); j++) {
                    JSONObject cObj = commentsArray.getJSONObject(j);
                    // Robust fallback for comments
                    String cUser = cObj.getString("username");
                    String cContent = cObj.getString("content");
                    
                    if (cObj.has("userId")) {
                        Comment newComment = new Comment(cObj.getString("userId"), cUser, cContent);
                        if (cObj.has("avatarUrl")) newComment.setAvatarUrl(cObj.getString("avatarUrl"));
                        post.addComment(newComment);
                    } else {
                        post.addComment(new Comment(cUser, cContent)); // Legacy
                    }
                }
            }
            list.add(post);
        }
        return list;
    }
    
    // Deserialize JSON String to List<User>
    private List<User> deserializeUsers(String jsonString) throws JSONException {
        List<User> list = new ArrayList<>();
        if (jsonString == null || jsonString.isEmpty()) return list;

        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String uid = obj.getString("userId");
            String uname = obj.getString("username");
            String avatar = obj.has("avatarUrl") ? obj.getString("avatarUrl") : null;
            User user = new User(uid, uname, avatar);
            list.add(user);
        }
        return list;
    }
}