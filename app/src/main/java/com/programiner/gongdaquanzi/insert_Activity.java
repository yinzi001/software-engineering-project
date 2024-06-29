package com.programiner.gongdaquanzi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programiner.gongdaquanzi.Adapter.BlogcreateAdapter;
import com.programiner.gongdaquanzi.Adapter.util.MySql_blogdatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class insert_Activity extends AppCompatActivity {

    private final List<Bitmap> bitmapList = new ArrayList<>();
    private BlogcreateAdapter blogcreateAdapter; // 定义适配器
    private static final int IMAGE_CAPTURE = 1;
    private static final int IMAGE_SELECT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(...) 设置布局
        // 初始化RecyclerView和BlogcreateAdapter
        setContentView(R.layout.activity_insert); // 确保这里使用的是正确的布局文件名
        RecyclerView recyclerView = findViewById(R.id.send_recycler);
        blogcreateAdapter = new BlogcreateAdapter(this, bitmapList); // 假设你的适配器接受bitmapList作为数据源
        recyclerView.setAdapter(blogcreateAdapter);
        // 设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button createButton = findViewById(R.id.publish);

       createButton.setOnClickListener(v -> {

           final SharedPreferences sharedPreferences = getSharedPreferences("AppName", MODE_PRIVATE);
           final String userIdStr = sharedPreferences.getString("userId", "");
           final int userId; // 将userId声明为final
           try {
               if (!userIdStr.isEmpty()) {
                   userId = Integer.parseInt(userIdStr);
               } else {
                   userId = 0; // 默认值在这里设置
               }
           } catch (NumberFormatException e) {
               e.printStackTrace();
               return; // 如果发生异常，直接返回，不执行后续操作
           }

           EditText titleEditText = findViewById(R.id.et_title);
           EditText contentEditText = findViewById(R.id.et_content);
           String title = titleEditText.getText().toString();
           String content = contentEditText.getText().toString();


                   try {
                       // 假设有一个方法可以创建Blog对象并插入数据库
                       // 这里的createBlogFromUI和insertBlog方法需要你根据实际情况实现
                       MySql_blogdatabase.DBHelper dbHelper = new MySql_blogdatabase.DBHelper();
                       MySql_blogdatabase.Blog blog;
                       if (!bitmapList.isEmpty()) {
                           Bitmap imageBitmap = bitmapList.get(0); // 只取第一个Bitmap

                           byte[] imageBlob = MySql_blogdatabase.DBHelper.BitmapToBlobSize(imageBitmap, 1024 * 1024);

                           blog = dbHelper.createBlogFromUI(userId, title, content, imageBlob);
                       } else {
                           // 如果没有图片，可以传递null或者一个空的Bitmap对象
                           blog = dbHelper.createBlogFromUI(userId, title, content, null);
                       }
                       dbHelper.insertBlog(blog, null);

                       // 插入成功，回到主线程显示Toast消息并跳转
                       runOnUiThread(() -> {
                           Toast.makeText(getApplicationContext(), "博客上传成功！", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(insert_Activity.this, navi_Activity.class);
                           startActivity(intent);
                       });
                   } catch (Exception e) {
                       e.printStackTrace();
                       runOnUiThread(() -> Toast.makeText(getApplicationContext(), "上传失败：" + e.getMessage(), Toast.LENGTH_SHORT).show());
                   }

            Intent intent = new Intent(insert_Activity.this, navi_Activity.class );
            startActivity(intent);

        });

        ImageView back = findViewById(R.id.icon_back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(insert_Activity.this, navi_Activity.class );
            startActivity(intent);
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == IMAGE_SELECT) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == IMAGE_CAPTURE) {
                // 处理拍照返回的数据
            }
            if (bitmap != null) {
                bitmapList.add(bitmap);
                blogcreateAdapter.notifyDataSetChanged();
            }
        }
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_SELECT);
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        Uri photoURI = Uri.fromFile(photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    // 其他方法...
}