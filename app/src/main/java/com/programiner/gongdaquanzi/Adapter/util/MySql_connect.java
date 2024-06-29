package com.programiner.gongdaquanzi.Adapter.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `password` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `name` varchar(255) DEFAULT '工大人',
  `user_image` longblob,
  PRIMARY KEY (`id`),
  KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2021111486 DEFAULT CHARSET=utf8mb3 COMMENT='用户 账号/密码表'
;
 */
public class MySql_connect {
    public class User {
        private int id;
        private String password;
        private String name;
        private byte[] userImage;

        // 构造函数
        public User() {
        }

        public User(int id, String password, String name, byte[] userImage) {
            this.id = id;
            this.password = password;
            this.name = name;
            this.userImage = userImage;
        }

        // id的getter和setter
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        // password的getter和setter
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        // name的getter和setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // userImage的getter和setter
        public byte[] getUserImage() {
            return userImage;
        }

        public void setUserImage(byte[] userImage) {
            this.userImage = userImage;
        }
    }
    public static Connection getConnection(String dbName) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); //加载驱动
            String ip = "rm-cn-84a3socgp000fego.rwlb.rds.aliyuncs.com";
            conn = (Connection) DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":3306/" + dbName,
                    "root", "Fq123456");
        } catch (SQLException ex) {//错误捕捉
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return conn;//返回Connection型变量conn用于后续连接
    }


    //增加users数据
    public static int insertIntoData(final String id, final String password) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection("gongdaquanzi");
            conn.setAutoCommit(false); // 开启事务，确保数据一致性

            // 插入用户数据
            String sqlUser = "INSERT INTO users (id, password) VALUES('" + id + "','" + password + "')";
            Statement stmtUser = conn.createStatement();
            int userInsertResult = stmtUser.executeUpdate(sqlUser);

            if (userInsertResult > 0) {
                // 插入对应的空profile数据
                String sqlProfile = "INSERT INTO profile (id, sex, name, image) VALUES (?, NULL, NULL, NULL)";
                PreparedStatement pstmtProfile = conn.prepareStatement(sqlProfile);
                pstmtProfile.setInt(1, Integer.parseInt(id));
                int profileInsertResult = pstmtProfile.executeUpdate();

                if (profileInsertResult > 0) {
                    conn.commit(); // 如果用户和profile都成功添加，提交事务
                    return userInsertResult;
                } else {
                    conn.rollback(); // 如果profile添加失败，回滚事务
                    return 0;
                }
            } else {
                conn.rollback(); // 如果用户添加失败，回滚事务
                return 0;
            }
        } catch (SQLException e) {
            conn.rollback(); // 发生异常，回滚事务
            throw e; // 继续抛出异常，让调用者知道发生了错误
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // 恢复自动提交
                conn.close(); // 关闭连接
            }
        }
    }

    // 替换指定id的用户记录中的user_image
    public static int updateUserImage(final int id, final byte[] userImage) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection("gongdaquanzi");
            String sql = "UPDATE users SET user_image = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);

            // 设置PreparedStatement的参数
            pstmt.setBytes(1, userImage); // 将byte[]设置到SQL语句的第一个占位符
            pstmt.setInt(2, id); // 将id设置到SQL语句的第二个占位符

            // 执行更新操作
            return pstmt.executeUpdate();
        } finally {
            // 确保资源被关闭，防止资源泄露
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    //查询users
    public static String querycol(final String id) {
        String a = null;
        try (Connection conn = getConnection("gongdaquanzi");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select password from users where id='" + id + "'")) {
            if (rs.first()) {
                a = rs.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return a;
    }

    // 查询指定id的用户记录中是否包含user_image
    public static boolean hasUserImage(final int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection("gongdaquanzi");
            String sql = "SELECT user_image FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(sql);

            // 设置PreparedStatement的参数
            pstmt.setInt(1, id);

            // 执行查询
            rs = pstmt.executeQuery();

            // 检查查询结果
            if (rs.next()) {
                byte[] userImage = rs.getBytes("user_image");
                return userImage != null && userImage.length > 0;
            }
            return false;
        } finally {
            // 确保资源被关闭，防止资源泄露
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    // 测试数据库连接
    public static String testConnection() {
        try {
            Connection conn = getConnection("gongdaquanzi");
            if (conn != null) {
                return "数据库连接成功";
            } else {
                return "数据库连接失败，连接对象为null";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "数据库连接失败，异常信息：" + e.getMessage();
        }
    }

    //根据id查头像/姓名 头像采用byte[],name为string
    // 在Android Studio中，使用新线程执行数据库操作，并通过回调接口返回结果
    public static Pair<byte[], String> queryUserImageAndName(final int userId) {
        try (Connection conn = getConnection("gongdaquanzi")) {
            String sql = "SELECT name, user_image FROM users WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        byte[] imageData = rs.getBytes("user_image");
                        return new Pair<>(imageData, name); // 返回用户名和图片
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 查询失败或未找到用户信息时返回null
    }

}
