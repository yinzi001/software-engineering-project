package com.programiner.gongdaquanzi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.programiner.gongdaquanzi.Adapter.util.MySql_profiledatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;


public class me_Activity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private String userIdStr;
    private MySql_profiledatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        SharedPreferences sharedPreferences = getSharedPreferences("AppName", MODE_PRIVATE);
        userIdStr = sharedPreferences.getString("userId", "");
        dbHelper = new MySql_profiledatabase();

        initUI();
        loadAndDisplayUserInfo();
    }

    private void initUI() {
        ImageView profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(v -> selectImage());

        findViewById(R.id.front).setOnClickListener(v -> navigateTo(navi_Activity.class));
        findViewById(R.id.insert_button).setOnClickListener(v -> navigateTo(insert_Activity.class));
        findViewById(R.id.profile).setOnClickListener(v -> navigateTo(profile_Activity.class));
        findViewById(R.id.friends).setOnClickListener(v -> navigateTo(friends_Activity.class));
    }

    private void loadAndDisplayUserInfo() {
        int userId = !userIdStr.isEmpty() ? Integer.parseInt(userIdStr) : 0;
        new Thread(() -> {
            MySql_profiledatabase.Profile profile = dbHelper.getProfile(userId);
            byte[] imageBlob = profile.getImage();
            runOnUiThread(() -> {
                ImageView profileImage = findViewById(R.id.profile_image);
                if (imageBlob != null && imageBlob.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
                    profileImage.setImageBitmap(bitmap);
                } else {
                    // 设置默认图片
                    profileImage.setImageResource(R.drawable.female_region); // 假设你有一个default_image.png在drawable资源中
                }
                TextView nameTextView = findViewById(R.id.name);
                nameTextView.setText(profile.getName());
                nameTextView.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // 使用profile里的压缩函数压缩图片
                byte[] imageBlob = MySql_profiledatabase.BitmapToBlobSize(bitmap, 1024 * 1024); // 假设最大Blob大小为1MB

                int userId = Integer.parseInt(userIdStr);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 在这里执行数据库操作或网络请求
                        try {
                            // 假设这是更新图片的函数
                            dbHelper.updateUserImage(userId, imageBlob);
                            // 更新UI，回到主线程
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 更新UI元素
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                // 更新UI显示压缩后的图片
                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
                ImageView profileImage = findViewById(R.id.profile_image);
                profileImage.setImageBitmap(compressedBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "文件未找到", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(me_Activity.this, cls);
        startActivity(intent);
    }
}