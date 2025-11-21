package com.example.limenforum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.limenforum.data.model.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化用户管理器
        User.init(getApplicationContext());

        // 如果已经登录过，直接跳到主页
        if (User.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.etUsername);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String name = etUsername.getText().toString().trim();
            if (!name.isEmpty()) {
                User.getInstance().login(name);
                Toast.makeText(this, "欢迎回来, " + name, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            }
        });
    }
}