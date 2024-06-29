package com.programiner.gongdaquanzi.Adapter.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class MySql_blogdatabase {

    public static class Blog {
        private int id;
        private int userId;
        private String title;
        private String content;
        private byte[] image_data;
        private Timestamp createdAt;


        // 添加一个构造函数
        public Blog(int id, int userId, String title, String content, Timestamp createdAt,byte[] imageData) {
            this.id = id;
            this.userId = userId;
            this.title = title;
            this.content = content;
            this.createdAt = createdAt;
            this.image_data = imageData;
        }

        // Getter和Setter方法
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public byte[] getImageData() {
            return image_data;
        }

        public void setImageData(byte[] imageData) {
            this.image_data = imageData;
        }
        public Timestamp getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Timestamp createdAt) {
            this.createdAt = createdAt;
        }

    }
    public static class DBHelper {
        private Connection connect() {
            // 使用MySql_blogdatabase类的getConnection方法连接数据库
            return MySql_connect.getConnection("gongdaquanzi");
        }

        //获取所有的blog
        public List<Blog> getAllBlogs() {
            List<Blog> blogs = new ArrayList<>();
            try (Connection conn = connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM blogs")) {
                while (rs.next()) {

                    Blog blog = new Blog(rs.getInt("id"), rs.getInt("user_id"), rs.getString("title"), rs.getString("content"), rs.getTimestamp("created_at"), rs.getBytes("image_data"));
                    blogs.add(blog);

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return blogs;
        }

        // 根据userId返回所有满足条件的blog
        public List<Blog> getBlogsByUserId(int userId) {
            List<Blog> blogs = new ArrayList<>();
            String sql = "SELECT * FROM blogs WHERE user_id = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Blog blog = new Blog(rs.getInt("id"), rs.getInt("user_id"), rs.getString("title"), rs.getString("content"), rs.getTimestamp("created_at"), rs.getBytes("image_data"));
                        blogs.add(blog);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return blogs;
        }


        //将blob数据转化为bitimap
        public Bitmap blobToBitmap(Blob blob) throws Exception {
            byte[] blobBytes = blob.getBytes(1, (int) blob.length());
            return BitmapFactory.decodeByteArray(blobBytes, 0, blobBytes.length);
        }

        // 压缩Bitmap到指定大小的Blob（以字节数组形式）
        public static byte[] BitmapToBlobSize(Bitmap bitmap, int maxBlobSize) {
            int quality = 100;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();

            // 循环减少质量以满足Blob大小要求
            while (imageData.length > maxBlobSize && quality > 0) {
                byteArrayOutputStream.reset(); // 重置ByteArrayOutputStream以清除之前的压缩数据
                quality -= 5; // 每次减少5%的质量
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                imageData = byteArrayOutputStream.toByteArray();
            }

            if (quality == 0 && imageData.length > maxBlobSize) {
                // 如果达到最低质量但仍然超过最大大小，则抛出异常
                throw new RuntimeException("无法将图片压缩到指定的Blob大小内");
            }

            return imageData; // 返回压缩后的字节数组，可以被视为Blob
        }
        //向数据库存入一个blog
        public void insertBlog(@NonNull Blog blog, Context context) {
            new Thread(() -> {
                String sql = "INSERT INTO blogs (user_id, title, content, created_at, image_data) VALUES (?, ?, ?, ?, ?)";
                try (Connection conn = connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, blog.getUserId());
                    pstmt.setString(2, blog.getTitle());
                    pstmt.setString(3, blog.getContent());
                    pstmt.setTimestamp(4, blog.getCreatedAt());
                    if (blog.getImageData() != null) {
                        byte[] imageData = blog.getImageData();
                        byte[] bytesData = new byte[imageData.length];
                        for (int i = 0; i < imageData.length; i++) {
                            bytesData[i] = imageData[i]; // 自动拆箱
                        }
                        pstmt.setBytes(5, bytesData);
                    } else {
                        pstmt.setNull(5, java.sql.Types.BLOB);
                    }
                    pstmt.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 在主线程中显示Toast
                    ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "插入博客失败", Toast.LENGTH_SHORT).show());
                }
            }).start();
        }
        //向blog里填入信息
        public Blog createBlogFromUI(int userId, String title, String content, byte[] imageBitmap) throws Exception {
            // 使用UUID生成唯一的blogId
            String uniqueId = UUID.randomUUID().toString();
            int blogId = uniqueId.hashCode();

            // 设置时区为中国时区
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(timeZone);

            // 获取当前时间，并格式化为中国时间
            String formattedDate = sdf.format(new java.util.Date());
            Timestamp createdAt = Timestamp.valueOf(formattedDate);

            // 直接插入数据库的逻辑应该在这个方法之外处理，这里只负责创建Blog对象
            return new Blog(blogId, userId, title, content, createdAt, imageBitmap );
        }


    }

}
