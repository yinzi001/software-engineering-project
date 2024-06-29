package com.programiner.gongdaquanzi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.programiner.gongdaquanzi.Adapter.util.MySql_connect;
import com.programiner.gongdaquanzi.Adapter.util.ToolsUtil;

public class loginActivity extends AppCompatActivity {

    public static int conn_on=0; // 用于判断连接是否成功
    public static String password_receive; // 用于接收数据库查询的返回数据
    // 定义消息类型
    private static final int CONNECTION_SUCCESS = 1;
    private static final int CONNECTION_FAIL = 2;

    // Handler实例化为类成员变量
    final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case CONNECTION_SUCCESS:
                    Toast.makeText(loginActivity.this, "网络连接成功", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_FAIL:
                    Toast.makeText(loginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Button login = this.findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = ToolsUtil.editTextToString(loginActivity.this.findViewById(R.id.username));
                String password = ToolsUtil.editTextToString(loginActivity.this.findViewById(R.id.password));

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(loginActivity.this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(() -> {
                        checkConnection();
                        String dbPassword = MySql_connect.querycol(username);
                        if (dbPassword != null && dbPassword.equals(password)) {
                            // 用户验证成功
                            runOnUiThread(() -> {

                                // 保存用户名（ID）到 SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("AppName", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userId", username); // 使用用户名作为 ID
                                editor.apply();

                                Intent intent = new Intent(loginActivity.this, navi_Activity.class);

                                startActivity(intent);
                                finish();
                            });
                        } else {
                            // 用户验证失败
                            runOnUiThread(() -> Toast.makeText(loginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show());
                        }
                    }).start();
                }
            }

        });

        Button logup = this.findViewById(R.id.logup_button);
        logup.setOnClickListener(v -> {
            Intent intent = new Intent(loginActivity.this, logupActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkConnection() {
        new Thread(() -> {
            try {
                MySql_connect.getConnection("gongdaquanzi"); // 尝试连接数据库
                // 连接成功，发送成功消息
                handler.sendEmptyMessage(CONNECTION_SUCCESS);
            } catch (Exception e) {
                // 连接失败，发送失败消息
                handler.sendEmptyMessage(CONNECTION_FAIL);
            }
        }).start();
    }
}