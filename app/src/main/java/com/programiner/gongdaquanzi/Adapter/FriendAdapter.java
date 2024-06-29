package com.programiner.gongdaquanzi.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.programiner.gongdaquanzi.R;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<Triple<Integer, byte[], String>> userList; // 用户列表数据，包含用户ID、用户图像和名称
    private Context context; // 上下文
    private OnFriendClickListener listener; // 点击事件监听器接口

    // 点击事件监听器接口
    public interface OnFriendClickListener {
        void onFriendClick(Triple<Integer, byte[], String> userTriple);
    }

    // 构造函数
    public FriendAdapter(Context context, List<Triple<Integer, byte[], String>> userList, OnFriendClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 从XML文件中加载布局
        View view = LayoutInflater.from(context).inflate(R.layout.friend_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 绑定数据到布局中
        Triple<Integer, byte[], String> user = userList.get(position);
        byte[] userImage = user.second;
        String userName = user.third;

        if (userImage != null) {
            holder.friendAvatar.setImageBitmap(BitmapFactory.decodeByteArray(userImage, 0, userImage.length));
        }
        holder.friendName.setText(userName); // 显示用户名

        holder.itemView.setOnClickListener(v -> {
            // 处理点击事件，例如打开聊天界面
            if (listener != null) {
                listener.onFriendClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        // 返回列表项的总数
        return userList.size();
    }

    // 提供对列表项中所有视图的访问
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView friendAvatar; // 朋友头像
        TextView friendName; // 朋友名字
        ImageView friendChatIcon; // 聊天图标

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 从itemView中找到视图
            friendAvatar = itemView.findViewById(R.id.friend_avatar);
            friendName = itemView.findViewById(R.id.friend_name);
            friendChatIcon = itemView.findViewById(R.id.friend_chat_icon);
        }
    }

    // 用于存储三个元素的简单泛型类
    public static class Triple<T, U, V> {
        public final T first;
        public final U second;
        public final V third;

        public Triple(T first, U second, V third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public static <T, U, V> Triple<T, U, V> create(T first, U second, V third) {
            return new Triple<>(first, second, third);
        }
    }
}