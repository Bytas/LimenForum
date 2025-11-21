package com.example.limenforum;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.limenforum.data.model.Comment;
import com.example.limenforum.data.model.Post;
import com.example.limenforum.data.model.User;
import com.example.limenforum.data.service.PostService;
import com.example.limenforum.ui.adapter.CommentAdapter;
import com.example.limenforum.utils.TimeUtils;

public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST = "extra_post";

    private Post post;
    private CommentAdapter commentAdapter;
    private PostService postService;

    private ImageView detailImage, toolbarUserAvatar;
    private TextView detailTitle, detailContent, detailTags, detailDate, detailCommentCount, toolbarUserName;
    private RecyclerView commentsRecyclerView;
    private EditText inputComment;
    private Button btnSendComment;
    private ImageView btnLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        
        postService = LimenApplication.getInstance().getPostService();

        // 1. 获取传递的数据
        post = (Post) getIntent().getSerializableExtra(EXTRA_POST);
        if (post == null) {
            finish();
            return;
        }

        // 2. 初始化视图
        initViews();

        // 3. 绑定数据
        bindData();

        // 4. 设置评论列表
        setupCommentsList();

        // 5. 设置事件监听
        setupListeners();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title to use custom layout
        }
        
        // Ensure navigation click listener is set explicitly as fallback
        toolbar.setNavigationOnClickListener(v -> finish());

        toolbarUserName = findViewById(R.id.toolbarUserName);
        toolbarUserAvatar = findViewById(R.id.toolbarUserAvatar);

        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailContent = findViewById(R.id.detailContent);
        detailTags = findViewById(R.id.detailTags);
        detailDate = findViewById(R.id.detailDate);
        detailCommentCount = findViewById(R.id.detailCommentCount);
        
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        inputComment = findViewById(R.id.inputComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnLike = findViewById(R.id.btnLike);
    }

    private void bindData() {
        // Toolbar User Info
        toolbarUserName.setText(post.getUsername()); // Uses resolved username
        
        if (post.getDisplayAvatarUrl() != null && !post.getDisplayAvatarUrl().isEmpty()) {
             Glide.with(this)
                .load(post.getDisplayAvatarUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .circleCrop()
                .into(toolbarUserAvatar);
        } else {
            toolbarUserAvatar.setImageResource(R.mipmap.ic_launcher_round);
        }

        // Post Content
        detailTitle.setText(post.getTitle());
        detailContent.setText(post.getContent());
        detailTags.setText("#" + post.getTagName());
        detailDate.setText(TimeUtils.formatTimeAgo(post.getTimestamp()));
        updateCommentCount();
        updateLikeButtonState();

        // Image
        if (post.getImageUri() != null && !post.getImageUri().isEmpty()) {
            detailImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(post.getImageUri())
                    .into(detailImage);
        } else {
            detailImage.setVisibility(View.GONE);
        }
    }

    private void setupCommentsList() {
        commentAdapter = new CommentAdapter();
        commentAdapter.setComments(post.getComments());
        
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);
        // NestedScrollingEnabled is set to false in XML to allow smooth scrolling within NestedScrollView
    }

    private void setupListeners() {
        btnSendComment.setOnClickListener(v -> {
            String text = inputComment.getText().toString().trim();
            if (!text.isEmpty()) {
                // Create Comment
                // Use current user ID for comment
                Comment newComment = new Comment(User.getInstance().getUserId(), User.getInstance().getUsername(), text);
                // Set avatar URL for immediate display
                newComment.setAvatarUrl(User.getInstance().getAvatarUrl());
                
                postService.addComment(post.getId(), newComment, new PostService.VoidCallback() {
                    @Override
                    public void onSuccess() {
                        // Update Adapter (which updates the list)
                        commentAdapter.addComment(newComment);
                        post.setCommentCount(post.getCommentCount() + 1); // Manually update count
                        
                        // Clear Input
                        inputComment.setText("");
                        
                        updateCommentCount();
                        Toast.makeText(PostDetailActivity.this, "评论发送成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(PostDetailActivity.this, "评论失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        
        btnLike.setOnClickListener(v -> {
            boolean newState = !post.isLiked();
            // Optimistic update
            post.setLiked(newState);
            if (newState) {
                post.setLikeCount(post.getLikeCount() + 1);
            } else {
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            }
            updateLikeButtonState();
            
            postService.toggleLike(post.getId(), newState, new PostService.VoidCallback() {
                @Override
                public void onSuccess() {
                    // Success
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Revert
                    post.setLiked(!newState);
                    if (!newState) {
                        post.setLikeCount(post.getLikeCount() + 1);
                    } else {
                        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                    }
                    updateLikeButtonState();
                    Toast.makeText(PostDetailActivity.this, "操作失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateCommentCount() {
        detailCommentCount.setText("共 " + post.getCommentCount() + " 条评论");
    }
    
    private void updateLikeButtonState() {
        if (post.isLiked()) {
            btnLike.setImageResource(R.drawable.ic_heart_filled);
            btnLike.setColorFilter(null); // No tint for filled heart
        } else {
            btnLike.setImageResource(R.drawable.ic_heart_outline);
            btnLike.setColorFilter(null); // Color is in the drawable itself
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close activity on Back arrow click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}