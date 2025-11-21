package com.example.limenforum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.limenforum.data.model.Post;
import com.example.limenforum.data.model.User;
import com.example.limenforum.data.service.ImageService;
import com.example.limenforum.data.service.PostService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private ImageView imgPreview, btnRemoveImage;
    private LinearLayout layoutAddImagePlaceholder;
    private ChipGroup chipGroupTags;
    private Button btnPublish;
    private TextView btnCancel;
    private CardView cardImageUpload;

    private Uri selectedImageUri = null;
    private PostService postService;
    private ImageService imageService;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    setImage(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postService = LimenApplication.getInstance().getPostService();
        imageService = LimenApplication.getInstance().getImageService();

        initViews();
        setupListeners();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        imgPreview = findViewById(R.id.imgPreview);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);
        layoutAddImagePlaceholder = findViewById(R.id.layoutAddImagePlaceholder);
        cardImageUpload = findViewById(R.id.cardImageUpload);
        chipGroupTags = findViewById(R.id.chipGroupTags);
        btnPublish = findViewById(R.id.btnPublish);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(v -> finish());

        cardImageUpload.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnRemoveImage.setOnClickListener(v -> removeImage());

        btnPublish.setOnClickListener(v -> publishPost());
    }

    private void setImage(Uri uri) {
        selectedImageUri = uri;
        imgPreview.setVisibility(View.VISIBLE);
        btnRemoveImage.setVisibility(View.VISIBLE);
        layoutAddImagePlaceholder.setVisibility(View.GONE);
        
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(imgPreview);
    }

    private void removeImage() {
        selectedImageUri = null;
        imgPreview.setVisibility(View.GONE);
        btnRemoveImage.setVisibility(View.GONE);
        layoutAddImagePlaceholder.setVisibility(View.VISIBLE);
    }

    private void publishPost() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入正文内容", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (title.isEmpty()) title = "无标题";

        String selectedTag = "其他";
        int checkedChipId = chipGroupTags.getCheckedChipId();
        if (checkedChipId != View.NO_ID) {
            Chip chip = findViewById(checkedChipId);
            selectedTag = chip.getText().toString();
        }

        btnPublish.setEnabled(false);
        btnPublish.setText("发布中...");

        if (selectedImageUri != null) {
            // Upload Image First
            String finalTitle = title;
            String finalSelectedTag = selectedTag;
            imageService.uploadImage(selectedImageUri, new ImageService.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Image saved, now create post with file:// URL
                    createPostInService(finalTitle, content, finalSelectedTag, imageUrl);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(CreatePostActivity.this, "图片上传失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                    btnPublish.setEnabled(true);
                    btnPublish.setText("发布");
                }
            });
        } else {
            // No image, direct create
            createPostInService(title, content, selectedTag, null);
        }
    }

    private void createPostInService(String title, String content, String tagName, String imageUrl) {
        Post newPost = new Post(User.getInstance().getUserId(), title, content, tagName, "刚刚", 0, 0);
        if (imageUrl != null) {
            newPost.setImageUri(imageUrl);
        }

        postService.createPost(newPost, new PostService.VoidCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(CreatePostActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                User.getInstance().incrementPostCount();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CreatePostActivity.this, "发布失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                btnPublish.setEnabled(true);
                btnPublish.setText("发布");
            }
        });
    }
}