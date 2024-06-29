package com.programiner.gongdaquanzi.Adapter;
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

import com.programiner.gongdaquanzi.Adapter.util.MySql_connect;
import com.programiner.gongdaquanzi.R;
import com.programiner.gongdaquanzi.Adapter.util.MySql_blogdatabase;
import com.programiner.gongdaquanzi.Adapter.util.ToolsUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private final List<MySql_blogdatabase.Blog> blogs;
    private final Context context;

    public SearchAdapter(Context context, List<MySql_blogdatabase.Blog> blogs) {
        this.context = context;
        this.blogs = blogs;
    }
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_blogview, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        MySql_blogdatabase.Blog blog = blogs.get(position);
        holder.title.setText(blog.getTitle());
        holder.content.setText(blog.getContent());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(blog.getCreatedAt());
        holder.createdAt.setText(formattedDate);

        if (blog.getImageData() != null) {
            holder.image.setImageBitmap(BitmapFactory.decodeByteArray(blog.getImageData(), 0, blog.getImageData().length));
            holder.image.setVisibility(View.VISIBLE);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        new Thread(() -> {
            try {
                Pair<byte[], String> userImageAndName = MySql_connect.queryUserImageAndName(blog.getUserId());
                byte[] userimage = userImageAndName.first;
                String username = userImageAndName.second;

                ((Activity) context).runOnUiThread(() -> {
                    if (userimage != null) {
                        holder.userimage.setImageBitmap(BitmapFactory.decodeByteArray(userimage, 0, userimage.length));
                        holder.userimage.setVisibility(View.VISIBLE);
                    } else {
                        holder.userimage.setVisibility(View.GONE);
                    }
                    holder.username.setText(username);
                });
            } catch (Exception e) {
                Log.e("SearchAdapter", "Error in thread", e);
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }




    static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, createdAt, username;
        ImageView image, userimage;

        @SuppressLint("WrongViewCast")
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.others_title);
            content = itemView.findViewById(R.id.others_text);
            createdAt = itemView.findViewById(R.id.time_text);
            image = itemView.findViewById(R.id.friend_images);
            userimage = itemView.findViewById(R.id.others_image);
            username = itemView.findViewById(R.id.others_name);
        }
    }
    public void updateData(List<MySql_blogdatabase.Blog> newBlogs) {
        blogs.clear();
        blogs.addAll(newBlogs);
    }

}
