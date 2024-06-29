package com.programiner.gongdaquanzi.Adapter.util;
import com.programiner.gongdaquanzi.Adapter.util.MySql_connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*

CREATE TABLE `friendships` (
  `friendshipID` int NOT NULL AUTO_INCREMENT,
  `userID1` int NOT NULL,
  `userID2` int NOT NULL,
  `status` enum('请求中','已接受') NOT NULL,
  `createdAT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`friendshipID`),
  KEY `fk_userID1` (`userID1`),
  KEY `fk_userID2` (`userID2`),
  CONSTRAINT `fk_userID1` FOREIGN KEY (`userID1`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_userID2` FOREIGN KEY (`userID2`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3
;

CREATE TABLE `message` (
	`messageID` int NOT NULL AUTO_INCREMENT,
	`senderID` int NOT NULL,
	`receiverID` int NOT NULL,
	`messageText` text NOT NULL,
	`createdAT` timestamp NULL ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`messageID`),
	CONSTRAINT `fk_senderID` FOREIGN KEY (`senderID`) REFERENCES `gongdaquanzi`.`users` (`id`),
	CONSTRAINT `fk_receiverID` FOREIGN KEY (`receiverID`) REFERENCES `gongdaquanzi`.`users` (`id`)
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8;


 */
public class MySql_friendshipsdatabase {

    private Connection connect() {
        // 使用MySql_connect类的getConnection方法连接数据库
        return MySql_connect.getConnection("gongdaquanzi");
    }

    //
    public boolean addFriendship(int userID1, int userID2, String status) {
        String sql = "INSERT INTO friendships (userID1, userID2, status) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID1);
            pstmt.setInt(2, userID2);
            pstmt.setString(3, status);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Friendship getFriendship(int friendshipID) {
        String sql = "SELECT friendshipID, userID1, userID2, status FROM friendships WHERE friendshipID = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, friendshipID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Friendship(rs.getInt("friendshipID"), rs.getInt("userID1"), rs.getInt("userID2"), rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Friendship> getAllAcceptedFriendships(int userID1) {
        List<Friendship> friendships = new ArrayList<>();                                       //OR userID2 = ?
        String sql = "SELECT friendshipID, userID1, userID2, status FROM friendships WHERE (userID1 = ? ) AND status = '已接受'";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID1);
           // pstmt.setInt(2, userID1);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                friendships.add(new Friendship(rs.getInt("friendshipID"), rs.getInt("userID1"), rs.getInt("userID2"), rs.getString("status")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return friendships;
    }

    public static class Friendship {
        private int friendshipID;
        private int userID1;
        private int userID2;
        private String status;

        public Friendship(int friendshipID, int userID1, int userID2, String status) {
            this.friendshipID = friendshipID;
            this.userID1 = userID1;
            this.userID2 = userID2;
            this.status = status;
        }

        // Getter和Setter方法
        public int getFriendshipID() {
            return friendshipID;
        }

        public int getUserID1() {
            return userID1;
        }

        public int getUserID2() {
            return userID2;
        }

        public String getStatus() {
            return status;
        }
    }
    public boolean sendMessage(int senderID, int receiverID, String messageText) {
        String sql = "INSERT INTO message (senderID, receiverID, messageText) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, senderID);
            pstmt.setInt(2, receiverID);
            pstmt.setString(3, messageText);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Message> getSendMessageHistory(int userID1, int userID2) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT messageID, senderID, receiverID, messageText, createdAT FROM message WHERE (senderID = ? AND receiverID = ?)  ORDER BY createdAT";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID1);
            pstmt.setInt(2, userID2);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(rs.getInt("messageID"), rs.getInt("senderID"), rs.getInt("receiverID"), rs.getString("messageText"), rs.getTimestamp("createdAT")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }
    public List<Message> getReceiveMessageHistory(int userID1, int userID2) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT messageID, senderID, receiverID, messageText, createdAT FROM message WHERE  (senderID = ? AND receiverID = ?) ORDER BY createdAT";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID2);
            pstmt.setInt(2, userID1);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(rs.getInt("messageID"), rs.getInt("senderID"), rs.getInt("receiverID"), rs.getString("messageText"), rs.getTimestamp("createdAT")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    public List<Message> getMessageHistory(int userID1, int userID2) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT messageID, senderID, receiverID, messageText, createdAT FROM message WHERE (senderID = ? AND receiverID = ?) OR (senderID = ? AND receiverID = ?) ORDER BY createdAT";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID1);
            pstmt.setInt(2, userID2);
            pstmt.setInt(3, userID2);
            pstmt.setInt(4, userID1);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(rs.getInt("messageID"), rs.getInt("senderID"), rs.getInt("receiverID"), rs.getString("messageText"), rs.getTimestamp("createdAT")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    public static class Message {
        private int messageID;
        private int senderID;
        private int receiverID;
        private String messageText;
        private java.sql.Timestamp createdAT;

        public Message(int messageID, int senderID, int receiverID, String messageText, java.sql.Timestamp createdAT) {
            this.messageID = messageID;
            this.senderID = senderID;
            this.receiverID = receiverID;
            this.messageText = messageText;
            this.createdAT = createdAT;
        }

        // Getter方法
        public int getMessageID() {
            return messageID;
        }

        public int getSenderID() {
            return senderID;
        }

        public int getReceiverID() {
            return receiverID;
        }

        public String getMessageText() {
            return messageText;
        }

        public java.sql.Timestamp getCreatedAT() {
            return createdAT;
        }

        // Setter方法
        public void setMessageID(int messageID) {
            this.messageID = messageID;
        }

        public void setSenderID(int senderID) {
            this.senderID = senderID;
        }

        public void setReceiverID(int receiverID) {
            this.receiverID = receiverID;
        }

        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }

        public void setCreatedAT(java.sql.Timestamp createdAT) {
            this.createdAT = createdAT;
        }
    }
}
