package com.programiner.gongdaquanzi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programiner.gongdaquanzi.Adapter.SearchAdapter;

import com.programiner.gongdaquanzi.Adapter.util.MySql_blogdatabase;
import com.programiner.gongdaquanzi.Adapter.util.ToolsUtil;
import java.util.ArrayList;
import java.util.List;

public class search_Activity extends AppCompatActivity {

    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Button searchButton = findViewById(R.id.search_Button);
        searchButton.setOnClickListener(v -> {

            // 初始化SearchView
            SearchView searchView = findViewById(R.id.search);
            String searchStr = "";

            if (searchView != null) {
                // 从SearchView获取查询字符串
                searchStr = searchView.getQuery().toString();
            } else {
                Log.e("search_Activity", "SearchView is null");
            }

            int searchInt = 0;
            try {
                searchInt = Integer.parseInt(searchStr);
            } catch (NumberFormatException e) {
                Log.e("search_Activity", "Error parsing search string to int", e);
                // 处理错误或设置searchInt为默认值
            }

            loadData(searchInt);

            setupRecyclerView();

        });

        // 找到LinearLayout并设置点击事件监听器
        ImageView back = findViewById(R.id.back); // 确保在你的layout XML中LinearLayout有一个id
        back.setOnClickListener(v -> {
            // 创建Intent来启动me_Activity
            Intent intent = new Intent(search_Activity.this, navi_Activity.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.blog_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 初始化适配器，此时可能还没有数据
        searchAdapter = new SearchAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(searchAdapter);
    }
    private void loadData(int search) {
        new Thread(() -> {
            try {
                MySql_blogdatabase.DBHelper dbHelper = new MySql_blogdatabase.DBHelper();
                List<MySql_blogdatabase.Blog> blogs = dbHelper.getBlogsByUserId(search);
                Log.d("navi_Activity", "Blogs loaded: " + blogs.size());
                runOnUiThread(() -> {
                    // 更新适配器数据
                    searchAdapter.updateData(blogs);
                    // 通知数据已更改
                    searchAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e("navi_Activity", "Error loading blogs", e);
            }
        }).start();
    }
}