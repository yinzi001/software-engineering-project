package com.programiner.gongdaquanzi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.programiner.gongdaquanzi.Adapter.util.MySql_connect;

import java.sql.SQLException;

public class logupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logup);
        //获取输入框账号密码
        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.logup_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(logupActivity.this, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(() -> {
                        try {
                            int result = MySql_connect.insertIntoData(username, password);
                            if (result > 0) {
                                runOnUiThread(() -> {
                                    Toast.makeText(logupActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(logupActivity.this, loginActivity.class);
                                    startActivity(intent);
                                    // 关闭当前活动
                                    finish();
                                });
                            } else {
                                runOnUiThread(() -> Toast.makeText(logupActivity.this, "注册失败，请重试！", Toast.LENGTH_SHORT).show());
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(logupActivity.this, "数据库错误：" + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }).start();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}