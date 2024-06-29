package com.programiner.gongdaquanzi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.programiner.gongdaquanzi.Adapter.util.MySql_profiledatabase;

public class profile_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("AppName", MODE_PRIVATE);
        String userIdStr = sharedPreferences.getString("userId", "");
        int userId = Integer.parseInt(userIdStr.isEmpty() ? "0" : userIdStr);

        EditText profileNameEditText = findViewById(R.id.profile_name);
        EditText profilePasswordEditText = findViewById(R.id.profile_password);
        EditText profileSexEditText = findViewById(R.id.profile_sex);

        ImageView profileNameChange = findViewById(R.id.profile_name_change);
        ImageView profilePasswordChange = findViewById(R.id.profile_password_change);
        ImageView profileSexChange = findViewById(R.id.profile_sex_change);

        profileNameChange.setOnClickListener(v -> updateUserInfo(userId, profileNameEditText.getText().toString(), "name"));
        profilePasswordChange.setOnClickListener(v -> updateUserInfo(userId, profilePasswordEditText.getText().toString(), "password"));
        profileSexChange.setOnClickListener(v -> updateUserInfo(userId, profileSexEditText.getText().toString(), "sex"));






        //back键
        ImageView back = findViewById(R.id.profile_back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(profile_Activity.this, me_Activity.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void updateUserInfo(int userId, String newValue, String fieldType) {
        new Thread(() -> {
            boolean updateResult = false;
            // 假设存在一个MySql_profiledatabase类用于处理数据库操作
            MySql_profiledatabase database = new MySql_profiledatabase();
            switch (fieldType) {
                case "name":
                    updateResult = database.updateUserName(userId,  newValue);
                    break;
                case "password":
                    // 假设updateProfile方法可以处理密码更新，这里简化为null
                    updateResult = database.updateProfile(userId, null, newValue, null);
                    break;
                case "sex":
                    updateResult = database.updateUserSex(userId, newValue);
                    break;
            }
            if (updateResult) {
                runOnUiThread(() -> Toast.makeText(profile_Activity.this, "修改成功", Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Toast.makeText(profile_Activity.this, "修改失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}