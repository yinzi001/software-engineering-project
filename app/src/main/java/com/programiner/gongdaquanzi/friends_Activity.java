package com.programiner.gongdaquanzi;
import com.programiner.gongdaquanzi.Adapter.util.MySql_connect;
import com.programiner.gongdaquanzi.Adapter.util.MySql_friendshipsdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.util.Pair;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programiner.gongdaquanzi.Adapter.FriendAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class friends_Activity extends AppCompatActivity {

    private Map<Integer, String> userIdToImagePathMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friends);

        // 获取SharedPreferences中的userId值，假设默认值为0
        // 获取SharedPreferences中的userId值，假设默认值为"0"（字符串）
        SharedPreferences sharedPreferences = getSharedPreferences("AppName", MODE_PRIVATE);
        // 首先尝试以字符串形式获取userId
        String userIdStr = sharedPreferences.getString("userId", "0");
        // 将userId声明为int类型
        int userId;
        try {
            // 将字符串转换为整数
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            // 如果转换失败，可能需要处理错误或设置默认值
            userId = 0; // 这里以0作为默认值
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置RecyclerView
        setupRecyclerView(userId);

        ImageView back = findViewById(R.id.friends_back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(friends_Activity.this, me_Activity.class);
            startActivity(intent);
        });
        //
        ImageView search = findViewById(R.id.friend_search);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(friends_Activity.this, navi_Activity.class);
            startActivity(intent);
        });

    }

    public void setupRecyclerView(int userId) {
        RecyclerView recyclerView = findViewById(R.id.friends_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MySql_friendshipsdatabase db1 = new MySql_friendshipsdatabase();
        MySql_connect db2 = new MySql_connect();
        List<FriendAdapter.Triple<Integer, byte[], String>> userList = new ArrayList<>();
        new Thread(() -> {

            // 数据库请求
            List<MySql_friendshipsdatabase.Friendship> friendList = db1.getAllAcceptedFriendships(userId);
            // 取出friendlist表中的user2ID
            for (MySql_friendshipsdatabase.Friendship friendship : friendList) {
                Pair<byte[], String> userInfo = MySql_connect.queryUserImageAndName(friendship.getUserID2());
                if (userInfo != null) {
                    // 保存图片到临时文件，并更新HashMap
                    File tempImageFile = saveImageToTempFile(userInfo.first);
                    if (tempImageFile != null) {
                        userIdToImagePathMap.put(friendship.getUserID2(), tempImageFile.getAbsolutePath());
                    }
                    userList.add(new FriendAdapter.Triple<>(friendship.getUserID2(), userInfo.first, userInfo.second));
                }
            }
            // 在UI线程中设置适配器
            runOnUiThread(() -> {
                FriendAdapter friendAdapter = new FriendAdapter(friends_Activity.this, userList, userTriple -> {
                    // 处理点击事件，例如打开聊天界面
                    Intent intent = new Intent(friends_Activity.this, friend_chat_Activity.class);
                    intent.putExtra("friendID", userTriple.first);
                    intent.putExtra("friendName", userTriple.third);
                    // 从HashMap中获取图片文件路径
                    String imagePath = userIdToImagePathMap.get(userTriple.first);
                    intent.putExtra("imageFilePath", imagePath);
                    startActivity(intent);

                });
                recyclerView.setAdapter(friendAdapter);
            });
        }).start();

    }
    private File saveImageToTempFile(byte[] imageData) {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("userImage", ".tmp", getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(imageData);
            fos.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}