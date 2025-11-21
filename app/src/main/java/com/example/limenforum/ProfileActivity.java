package com.example.limenforum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.limenforum.data.model.User;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        User user = User.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 显示返回箭头
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvName = findViewById(R.id.tvProfileName);
        TextView tvPosts = findViewById(R.id.tvPostCount);
        TextView tvLikes = findViewById(R.id.tvLikeCount);
        Button btnLogout = findViewById(R.id.btnLogout);

        // 绑定数据
        tvName.setText(user.getUsername());
        tvPosts.setText(String.valueOf(user.getPostCount()));
        tvLikes.setText(String.valueOf(user.getLikeCount()));

        btnLogout.setOnClickListener(v -> {
            user.logout();
            // 回到登录页，并清空返回栈
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}