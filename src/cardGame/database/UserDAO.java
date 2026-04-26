package cardGame.database;

import cardGame.entity.User;
import java.sql.*;

public class UserDAO {
    // 로그인 확인 메서드
    public User loginCheck(String id, String pw) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, pw);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    // DB의 컬럼명과 User 클래스의 필드를 연결합니다.
                    user.setName(rs.getString("nickname"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setGender(rs.getString("gender"));
                    // user.setGender(rs.getString("gender"));

                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 로그인 실패 시
    }

    // 회원가입 확인 메서드
    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (username, password, nickname, gender) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // DB 컬럼 순서에 맞게 User 객체의 데이터를 매핑합니다.
            // 현재 구조상: username(ID), password(PW), nickname(별명), gender(성별)
            pstmt.setString(1, user.getUsername()); // 상위 Player의 name(ID) 값
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());     // User의 nickname(별명) 값
            pstmt.setString(4, user.getGender());

            int result = pstmt.executeUpdate();
            return result > 0; // 저장 성공 시 true 반환

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // UserDAO.java에 추가
    public boolean isIdDuplicate(String id) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // 0보다 크면 중복
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}