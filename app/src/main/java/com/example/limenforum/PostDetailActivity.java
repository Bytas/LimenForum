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
import com.example.limenforum.ui.adapter.CommentAdapter;

import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST = "extra_post";

    private Post post;
    private CommentAdapter commentAdapter;

    private ImageView detailImage, toolbarUserAvatar;
    private TextView detailTitle, detailContent, detailTags, detailDate, detailCommentCount, toolbarUserName;
    private RecyclerView commentsRecyclerView;
    private EditText inputComment;
    private Button btnSendComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

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
    }

    private void bindData() {
        // Toolbar User Info
        toolbarUserName.setText(post.getUsername());
        // In a real app, load user avatar URL here

        // Post Content
        detailTitle.setText(post.getTitle());
        detailContent.setText(post.getContent());
        detailTags.setText("#" + post.getTagName());
        detailDate.setText(post.getTimeAgo());
        updateCommentCount();

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
                Comment newComment = new Comment(User.getInstance().getUsername(), text);
                
                // Add to Post Object (Note: This only modifies the local copy passed via Intent. 
                // In a real app with a DB/API, this would be handled centrally. 
                // For this mock app, changes here won't reflect back in MainActivity list unless we pass data back 
                // or use a Singleton data source. Given the scope, we'll just update this view.)
                
                // REMOVED post.addComment(newComment); to avoid double adding since adapter shares the list reference
                
                // Update Adapter (which updates the list)
                commentAdapter.addComment(newComment);
                post.setCommentCount(post.getCommentCount() + 1); // Manually update count

                
                // Clear Input
                inputComment.setText("");
                
                updateCommentCount();
                Toast.makeText(this, "评论发送成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCommentCount() {
        detailCommentCount.setText("共 " + post.getCommentCount() + " 条评论");
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
