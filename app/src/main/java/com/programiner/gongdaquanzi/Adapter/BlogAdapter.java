package com.programiner.gongdaquanzi.Adapter;

import static com.programiner.gongdaquanzi.Adapter.util.MySql_connect.queryUserImageAndName;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.programiner.gongdaquanzi.R;
import com.programiner.gongdaquanzi.Adapter.util.MySql_blogdatabase;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private List<MySql_blogdatabase.Blog> blogs;
    private Context context;

    public BlogAdapter(Context context, List<MySql_blogdatabase.Blog> blogs) {
        this.context = context;
        this.blogs = blogs;

    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_blogview, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        MySql_blogdatabase.Blog blog = blogs.get(position);
        holder.title.setText(blog.getTitle());
        holder.content.setText(blog.getContent());
        // 格式化时间戳为可读日期时间字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(blog.getCreatedAt());
        holder.createdAt.setText(formattedDate); // 设置时间戳

        //blog图片
        if (blog.getImageData() != null) {
            holder.image.setImageBitmap(BitmapFactory.decodeByteArray(blog.getImageData(), 0, blog.getImageData().length));
            holder.image.setVisibility(View.VISIBLE);
        }else {
            // 没有图片数据时，保持ImageView为gone
            holder.image.setVisibility(View.GONE);
        }

        //个人头像
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("BlogAdapter", "Thread started"); // 日志输出，确认线程启动
                    Pair<byte[], String> userImageAndName = queryUserImageAndName(blog.getUserId());
                    byte[] userimage = userImageAndName.first;
                    String username = userImageAndName.second;
                    Log.d("BlogAdapter", "Query executed"); // 日志输出，确认查询执行

                    ((Activity) context).runOnUiThread(() -> {
                        try {
                            Log.d("BlogAdapter", "Updating UI"); // 日志输出，确认UI更新
                            if (userimage != null) {
                                holder.userimage.setImageBitmap(BitmapFactory.decodeByteArray(userimage, 0, userimage.length));
                                holder.userimage.setVisibility(View.VISIBLE);
                            } else {
                                holder.userimage.setVisibility(View.GONE);
                            }
                            holder.username.setText(username);
                        } catch (Exception e) {
                            Log.e("BlogAdapter", "Error updating UI", e);
                        }
                    });
                } catch (Exception e) {
                    Log.e("BlogAdapter", "Error in thread", e);
                }
            }
        }).start();


    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

    public void updateData(List<MySql_blogdatabase.Blog> newBlogs) {
        blogs.clear(); // 清除现有数据
        blogs.addAll(newBlogs); // 添加新数据
        notifyDataSetChanged(); // 通知数据已更改
    }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, createdAt,username;
        ImageView image;
        ImageView userimage;

        @SuppressLint("WrongViewCast")
        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.others_title);
            content = itemView.findViewById(R.id.others_text);
            createdAt = itemView.findViewById(R.id.time_text);
            image = itemView.findViewById(R.id.friend_images);
            userimage = itemView.findViewById(R.id.others_image);
            username = itemView.findViewById(R.id.others_name);
        }
    }
}