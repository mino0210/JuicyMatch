package cardGame.database;

import cardGame.entity.User;
import java.sql.*;

public class UserDAO {
    
    
    public User loginCheck(String id, String pw) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, pw);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    
                    
                    user.setName(rs.getString("nickname"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setGender(rs.getString("gender"));
                    

                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    
    
    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (username, password, nickname, gender) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            
            
            
            
            pstmt.setString(1, user.getUsername()); 
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());     
            pstmt.setString(4, user.getGender());

            int result = pstmt.executeUpdate();
            return result > 0; 

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean isIdDuplicate(String id) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}