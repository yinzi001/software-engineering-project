package com.programiner.gongdaquanzi.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.programiner.gongdaquanzi.R;
import com.programiner.gongdaquanzi.insert_Activity;

import java.util.List;

public class BlogcreateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Bitmap> data;

    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;

    public BlogcreateAdapter(Context context, List<Bitmap> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_TWO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_item_final, parent, false);
            return new TypeTwoViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_item, parent, false);
            return new TypeOneViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TypeTwoViewHolder) {
            // TypeTwo specific binding logic here
            ((TypeTwoViewHolder) holder).imageView.setOnClickListener(v -> {
                if(context instanceof insert_Activity){
                    ((insert_Activity)context).selectImage();
                }
            });  } else if (holder instanceof TypeOneViewHolder) {
            // TypeOne specific binding logic here
            if (position < data.size()) { // 防止索引越界
                Bitmap bitmap = data.get(position);
                ((TypeOneViewHolder) holder).imageView.setImageBitmap(bitmap); // 设置图片
            }
            ((TypeOneViewHolder) holder).imageView.setOnClickListener(v -> Toast.makeText(context, "Type One Clicked", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1; // Assuming the last item is of TYPE_TWO
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= data.size()) {
            return TYPE_TWO;
        } else {
            return TYPE_ONE;
        }
    }

    public static class TypeOneViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public TypeOneViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView6);
        }
    }

    public static class TypeTwoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public TypeTwoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.defaultimg);
        }
    }
}