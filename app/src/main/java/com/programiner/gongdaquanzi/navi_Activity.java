package com.programiner.gongdaquanzi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.programiner.gongdaquanzi.Adapter.BlogAdapter;
import com.programiner.gongdaquanzi.Adapter.util.MySql_blogdatabase;

import java.util.ArrayList;
import java.util.List;

public class navi_Activity extends AppCompatActivity {
    private BlogAdapter blogAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navi);


        // 找到LinearLayout并设置点击事件监听器
        LinearLayout myLayout = findViewById(R.id.me); // 确保在你的layout XML中LinearLayout有一个id
        myLayout.setOnClickListener(v -> {
            // 创建Intent来启动me_Activity
            Intent intent = new Intent(navi_Activity.this, me_Activity.class);
            startActivity(intent);
        });
        // 找到LinearLayout并设置点击事件监听器
        ImageButton insert = findViewById(R.id.insert_button); // 确保在你的layout XML中LinearLayout有一个id
        insert.setOnClickListener(v -> {
            // 创建Intent来启动me_Activity
            Intent intent = new Intent(navi_Activity.this, insert_Activity.class);
            startActivity(intent);
        });
        ImageView search = findViewById(R.id.searchButton); // 确保在你的layout XML中LinearLayout有一个id
        search.setOnClickListener(v -> {
            // 创建Intent来启动me_Activity
            Intent intent = new Intent(navi_Activity.this, search_Activity.class);
            startActivity(intent);
        });

        setupSwipeRefreshLayout(); // 设置SwipeRefreshLayout
        setupRecyclerView(); // 设置RecyclerView
        loadData(); // 初次加载数据
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.blog_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 初始化适配器，此时可能还没有数据
        blogAdapter = new BlogAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(blogAdapter);
    }

    private void loadData() {
        new Thread(() -> {
            try {
                MySql_blogdatabase.DBHelper dbHelper = new MySql_blogdatabase.DBHelper();
                List<MySql_blogdatabase.Blog> blogs = dbHelper.getAllBlogs();
                Log.d("navi_Activity", "Blogs loaded: " + blogs.size());

                runOnUiThread(() -> {
                    // 更新适配器数据而不是直接操作UI
                    blogAdapter.updateData(blogs); // 需要在BlogAdapter中添加updateData方法
                    swipeRefreshLayout.setRefreshing(false); // 停止刷新动画
                });
            } catch (Exception e) {
                Log.e("navi_Activity", "Error loading blogs", e);
            }
        }).start();
    }


    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }
}