package com.programiner.gongdaquanzi.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.programiner.gongdaquanzi.Adapter.util.MySql_friendshipsdatabase;
import com.programiner.gongdaquanzi.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<MySql_friendshipsdatabase.Message> messages;
    // 修改为Bitmap类型
    private  Bitmap myImageBitmap;
    private  Bitmap friendImageBitmap;

    private final int VIEW_TYPE_SENT = 1;
    private final int VIEW_TYPE_RECEIVED = 2;

    // 修改构造函数参数类型为Bitmap
    public ChatAdapter(List<MySql_friendshipsdatabase.Message> messages, Bitmap myImageBitmap, Bitmap friendImageBitmap) {
        this.messages = messages;
        this.myImageBitmap = myImageBitmap;
        this.friendImageBitmap = friendImageBitmap;
    }

    @Override
    public int getItemViewType(int position) {
        MySql_friendshipsdatabase.Message message = messages.get(position);
        if (message.getSenderID() < message.getReceiverID()) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sended, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MySql_friendshipsdatabase.Message message = messages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message, myImageBitmap);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message, friendImageBitmap);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTime;
        ImageView profileImage;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.me_chat_text);
            messageTime = itemView.findViewById(R.id.me_chat_time);
            profileImage = itemView.findViewById(R.id.me_chat_image);
        }

        // 使用Bitmap设置图片
        void bind(MySql_friendshipsdatabase.Message message, Bitmap imageBitmap) {
            messageText.setText(message.getMessageText());
            // 检查日期对象是否为null
            if (message.getCreatedAT() != null) {
                // 如果日期对象不为null，则正常格式化并显示日期
                messageTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message.getCreatedAT()));
            } else {
                // 如果日期对象为null，则显示默认文本
                messageTime.setText("时间未知");
            }
            profileImage.setImageBitmap(imageBitmap);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTime;
        ImageView profileImage;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.friend_chat_text);
            messageTime = itemView.findViewById(R.id.friend_chat_time);
            profileImage = itemView.findViewById(R.id.friend_chat_image);
        }

        // 使用Bitmap设置图片
        void bind(MySql_friendshipsdatabase.Message message, Bitmap imageBitmap) {
            messageText.setText(message.getMessageText());
            messageTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message.getCreatedAT()));
            profileImage.setImageBitmap(imageBitmap);
        }
    }
    public void updateData(List<MySql_friendshipsdatabase.Message> newMessages,Bitmap myImage, Bitmap friendImage) {
        this.messages.clear(); // 清除旧数据
        this.messages.addAll(newMessages); // 添加新数据
        this.myImageBitmap = myImage;
        this.friendImageBitmap = friendImage;
        notifyDataSetChanged(); // 通知数据已更改
    }
}