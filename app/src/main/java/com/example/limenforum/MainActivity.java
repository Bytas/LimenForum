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
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.limenforum.data.model.Post;
import com.example.limenforum.data.model.User;
import com.example.limenforum.ui.home.HomeFragment;
import com.example.limenforum.ui.profile.ProfileFragment;

import com.example.limenforum.data.service.PostService;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navPublish, navMine;
    private android.net.Uri selectedImageUri = null;
    private ImageView tempPreviewImage;
    private PostService postService;

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
        
        // Ensure system bars are handled correctly, not overlapping content blindly
        // If we want standard behavior (status bar visible, not under content):
        // We can simply NOT call setDecorFitsSystemWindows(false) or handle insets.
        // But to be safe and explicit for "normal" app behavior:
        // WindowCompat.setDecorFitsSystemWindows(getWindow(), true); // Default is usually true for AppCompatActivity but good to ensure if issues arise.
        
        User.init(this);
        // Get Service from Application
        postService = LimenApplication.getInstance().getPostService();
        
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
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivity(intent);
    }
}