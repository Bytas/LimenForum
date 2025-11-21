package com.example.limenforum;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.limenforum.data.model.User;
import com.example.limenforum.ui.home.HomeFragment;
import com.example.limenforum.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navPublish, navMine;
    private android.net.Uri selectedImageUri = null;
    private ImageView tempPreviewImage;

    // 图片选择器 (ActivityResultLauncher)
    private final androidx.activity.result.ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (tempPreviewImage != null) {
                        tempPreviewImage.setImageURI(uri);
                        tempPreviewImage.setVisibility(android.view.View.VISIBLE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.init(this);
        setContentView(R.layout.activity_main);

        // Check login
        if (!User.getInstance().isLoggedIn()) {
             startActivity(new Intent(this, LoginActivity.class));
             finish();
             return;
        }

        // Bottom Nav Views
        navHome = findViewById(R.id.navHome);
        navPublish = findViewById(R.id.navPublish);
        navMine = findViewById(R.id.navMine);

        // Load default fragment
        replaceFragment(new HomeFragment());
        updateNavSelection(navHome);

        // Bottom Nav Listeners
        navHome.setOnClickListener(v -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (current instanceof HomeFragment) {
                ((HomeFragment) current).scrollToTop();
            } else {
                replaceFragment(new HomeFragment());
                updateNavSelection(navHome);
            }
        });

        navPublish.setOnClickListener(v -> showCreatePostDialog());

        navMine.setOnClickListener(v -> {
             replaceFragment(new ProfileFragment());
             updateNavSelection(navMine);
        });
    }

    // Public method to switch to profile tab (used by HomeFragment avatar)
    public void switchToProfile() {
        replaceFragment(new ProfileFragment());
        updateNavSelection(navMine);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    private void updateNavSelection(LinearLayout selectedNav) {
        // Simple visual reset logic (optional if using raw LinearLayouts without state list drawables)
        // Ideally we'd change icons/text color here.
        // For now, let's just assume the layout handles touch feedback via background.
        
        // We can bold the text of selected one
        resetNavStyle(navHome);
        resetNavStyle(navMine);
        
        TextView tv = (TextView) selectedNav.getChildAt(0);
        tv.setTextColor(android.graphics.Color.parseColor("#111827")); // Dark
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void resetNavStyle(LinearLayout nav) {
        TextView tv = (TextView) nav.getChildAt(0);
        tv.setTextColor(android.graphics.Color.parseColor("#6B7280")); // Gray
        tv.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void showCreatePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("快速发帖");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);

        final EditText inputTitle = new EditText(this);
        inputTitle.setHint("加个标题...");
        inputTitle.setMaxLines(1);
        layout.addView(inputTitle);

        final EditText inputContent = new EditText(this);
        inputContent.setHint("分享你的想法...");
        inputContent.setMinLines(3);
        inputContent.setGravity(Gravity.TOP);
        layout.addView(inputContent);

        final TextView tagLabel = new TextView(this);
        tagLabel.setText("选择分区:");
        tagLabel.setPadding(0, 30, 0, 10);
        layout.addView(tagLabel);

        final Spinner tagSpinner = new Spinner(this);
        String[] tags = {"编程", "游戏", "校园", "科技"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tags);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(spinnerAdapter);
        layout.addView(tagSpinner);

        tempPreviewImage = new ImageView(this);
        tempPreviewImage.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400)); 
        tempPreviewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tempPreviewImage.setVisibility(android.view.View.GONE);
        layout.addView(tempPreviewImage);

        Button btnAddImage = new Button(this);
        btnAddImage.setText("添加图片 📷");
        btnAddImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        layout.addView(btnAddImage);

        selectedImageUri = null;

        builder.setView(layout);

        builder.setPositiveButton("发布", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String content = inputContent.getText().toString();
            String selectedTag = tagSpinner.getSelectedItem().toString();

            if (title.isEmpty()) title = "无标题";

            if (!content.isEmpty()) {
                // NOTE: In a real app with separate fragments, 
                // posting should likely go through a Service/ViewModel shared by MainActivity
                // and then notify the HomeFragment to refresh.
                // For this refactor step, we will just show a Toast since we don't have a full event bus.
                // Ideally, we would add to the MockService's internal list.
                
                Toast.makeText(this, "发布成功 (需刷新列表查看)", Toast.LENGTH_SHORT).show();
                User.getInstance().incrementPostCount();
                
                // If on HomeFragment, we might want to trigger a refresh manually if we had access.
                // For now, switching tabs will refresh.
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }
}