package com.programiner.gongdaquanzi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programiner.gongdaquanzi.Adapter.ChatAdapter;
import com.programiner.gongdaquanzi.Adapter.util.MySql_blogdatabase;
import com.programiner.gongdaquanzi.Adapter.util.MySql_friendshipsdatabase;
import com.programiner.gongdaquanzi.Adapter.util.MySql_profiledatabase;

import java.util.ArrayList;
import java.util.List;

public class friend_chat_Activity extends AppCompatActivity {

    private ChatAdapter adapter;
    private RecyclerView recyclerView;
    private final MySql_profiledatabase db1 = new MySql_profiledatabase();
    private final MySql_friendshipsdatabase db2 = new MySql_friendshipsdatabase();
    private int currentUserId;
    private int friendID;
    private String friendName;
    private  byte[] temp1;
    private  byte[] temp2;

    private List<MySql_friendshipsdatabase.Message> messages;
    private Bitmap myImageBitmap ;
    private Bitmap friendImageBitmap ;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 调用查询聊天记录的函数
            searchMessage();
            // 每隔6秒再次执行这个Runnable，实现定时任务
            handler.postDelayed(this, 6000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);

        // 在这里初始化Bitmap
        myImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.female_region);
        friendImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.female_region);


        //获取userID
        SharedPreferences sharedPreferences = getSharedPreferences("AppName", MODE_PRIVATE);
        String userIdStr = sharedPreferences.getString("userId", "");
        currentUserId = !userIdStr.isEmpty() ? Integer.parseInt(userIdStr) : 0;
        //获取friendID
        Intent intent = getIntent();
        friendID = intent.getIntExtra("friendID", -1);


        recyclerView = findViewById(R.id.chat_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(new ArrayList<>(), myImageBitmap, friendImageBitmap);
        recyclerView.setAdapter(adapter);

        //查询图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                MySql_profiledatabase.Profile myprofile = db1.getProfileById(currentUserId);
                MySql_profiledatabase.Profile friendfile = db1.getProfileById(friendID);
                temp1 = myprofile.getImage();
                temp2 = friendfile.getImage();

                Log.d("ChatActivity", "Profile images fetched"); // 添加日志
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        friendName = friendfile.getName();
                        if (temp1 != null) {
                            myImageBitmap = BitmapFactory.decodeByteArray(temp1, 0, temp1.length);
                            Log.d("ChatActivity", "My image bitmap set"); // 添加日志
                        }
                        if (temp2 != null) {
                            friendImageBitmap = BitmapFactory.decodeByteArray(temp2, 0, temp2.length);
                            Log.d("ChatActivity", "Friend image bitmap set"); // 添加日志
                        }
                        // 在这里更新好友名字的UI
                        TextView friendNameTextView = findViewById(R.id.friend_name);
                        friendNameTextView.setText(friendName);
                    }
                });
            }
        }).start();

/*
        //查询聊天记录
        new Thread(new Runnable() {
            @Override
            public void run() {
                List< MySql_friendshipsdatabase.Message> temp_messages = db2.getMessageHistory(currentUserId, friendID);
                if(temp_messages!=null){
                    messages = temp_messages;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 创建并设置适配器，确保适配器的构造函数能接收这些参数
                            ChatAdapter adapter = new ChatAdapter(messages, myImageBitmap, friendImageBitmap);
                            RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                };
            }
        }).start();
*/

        // 绑定返回键
        ImageView backButton = findViewById(R.id.friend_chat_back);
        backButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(friend_chat_Activity.this, friends_Activity.class);
            startActivity(intent1);
        });


        // 绑定好友名字
        TextView friendNameTextView = findViewById(R.id.friend_name);
        // 假设你已经有了好友的名字，这里直接设置
        friendNameTextView.setText(friendName); // 实际应用中应从数据库或intent中获取

        // 绑定输入框和发送按钮
        EditText chatInput = findViewById(R.id.chat_input);
        Button sendButton = findViewById(R.id.chat_send_button);
        sendButton.setOnClickListener(v -> {
            String message = chatInput.getText().toString();
            // 这里添加发送消息的逻辑
            new Thread(new Runnable() {
                @Override
                public void run() {
                    db2.sendMessage(currentUserId,friendID,message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 确保在主线程中清空输入框
                            chatInput.setText("");
                        }
                    });
                }

            }).start();
        });

        handler.post(runnable);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Activity不可见时停止定时任务
        handler.removeCallbacks(runnable);
    }

    public void searchMessage(){
        //查询聊天记录
        new Thread(new Runnable() {
            @Override
            public void run() {
                List< MySql_friendshipsdatabase.Message> temp_messages = db2.getMessageHistory(currentUserId, friendID);
                if(temp_messages!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 创建并设置适配器，确保适配器的构造函数能接收这些参数
                            adapter.updateData(temp_messages, myImageBitmap, friendImageBitmap);
                        }
                    });
                };
            }
        }).start();
    }

}