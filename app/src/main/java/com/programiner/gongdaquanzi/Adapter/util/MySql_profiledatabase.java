package com.programiner.gongdaquanzi.Adapter.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySql_profiledatabase {


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

    public  Connection connect() {
        // 使用MySql_connect类的getConnection方法连接数据库
        return MySql_connect.getConnection("gongdaquanzi");
    }

    public void updateUserImage(final int id, final byte[] userImage) {
        String sqlProfile = "UPDATE profile SET image = ? WHERE id = ?";
        String sqlUsers = "UPDATE users SET user_image = ? WHERE id = ?";
        try (Connection conn = MySql_connect.getConnection("gongdaquanzi");
             PreparedStatement pstmtProfile = conn.prepareStatement(sqlProfile);
             PreparedStatement pstmtUsers = conn.prepareStatement(sqlUsers)) {

            // 更新profile表
            pstmtProfile.setBytes(1, userImage);
            pstmtProfile.setInt(2, id);
            pstmtProfile.executeUpdate();

            // 更新users表
            pstmtUsers.setBytes(1, userImage);
            pstmtUsers.setInt(2, id);
            pstmtUsers.executeUpdate();
        } catch (SQLException e) {
            System.out.println("更新图片时发生错误: " + e.getMessage());
        }
    }
    public boolean addProfile(int id, String sex, String name, byte[] image) {
        String sql = "INSERT INTO profile (id, sex, name, image) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, sex);
            pstmt.setString(3, name);
            pstmt.setBytes(4, image);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    //根据id获取性别 名字
    public Profile getProfile(int id) {
        String sql = "SELECT id, sex, name, image FROM profile WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Profile(rs.getInt("id"), rs.getString("sex"), rs.getString("name"), rs.getBytes("image"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // 更新用户的名字
    public boolean updateUserName(int id, String name) {
        String sqlProfile = "UPDATE profile SET name = ? WHERE id = ?";
        String sqlUsers = "UPDATE users SET name = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmtProfile = conn.prepareStatement(sqlProfile);
             PreparedStatement pstmtUsers = conn.prepareStatement(sqlUsers)) {
            // 更新profile表
            pstmtProfile.setString(1, name);
            pstmtProfile.setInt(2, id);
            pstmtProfile.executeUpdate();

            // 更新users表
            pstmtUsers.setString(1, name);
            pstmtUsers.setInt(2, id);
            pstmtUsers.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // 更新用户的性别
    public boolean updateUserSex(int id, String sex) {
        String sql = "UPDATE profile SET sex = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sex);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    //更新
    public boolean updateProfile(int id, String sex, String name, byte[] image) {
        String sqlProfile = "UPDATE profile SET sex = ?, name = ?, image = ? WHERE id = ?";
        String sqlUsers = "UPDATE users SET name = ?, user_image = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmtProfile = conn.prepareStatement(sqlProfile);
             PreparedStatement pstmtUsers = conn.prepareStatement(sqlUsers)) {
            // 更新profile表
            pstmtProfile.setString(1, sex);
            pstmtProfile.setString(2, name);
            pstmtProfile.setBytes(3, image);
            pstmtProfile.setInt(4, id);
            pstmtProfile.executeUpdate();

            // 更新users表
            pstmtUsers.setString(1, name);
            pstmtUsers.setBytes(2, image);
            pstmtUsers.setInt(3, id);
            pstmtUsers.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public MySql_profiledatabase.Profile getProfileById(int id) {
        String sql = "SELECT id, sex, name, image FROM profile WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new MySql_profiledatabase.Profile(
                            rs.getInt("id"),
                            rs.getString("sex"),
                            rs.getString("name"),
                            rs.getBytes("image")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("查询Profile时发生错误: " + e.getMessage());
        }
        return null;
    }

    public static class Profile {
        private int id;
        private String sex;
        private String name;
        private  byte[] image;

        public Profile(int id, String sex, String name, byte[] image) {
            this.id = id;
            this.sex = sex;
            this.name = name;
            this.image = image;
        }

        public byte[] getImage() {
            return image;
        }

        public void setImage(byte[] image) {
            this.image = image;
        }


        // Getter和Setter方法
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
/*
CREATE TABLE `profile` (
  `id` int NOT NULL,
  `sex` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'Unknown',
  `name` varchar(255) DEFAULT NULL,
  `image` longblob,
  PRIMARY KEY (`id`),
  CONSTRAINT `id` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3
;
 */