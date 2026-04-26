package cardGame.database;

import cardGame.entity.Record;
import cardGame.entity.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordDAO {

    /**
     * 게임 결과 저장 (INSERT)
     * level 컬럼이 추가된 쿼리로 수정되었습니다.
     */
    public boolean insertRecord(Record record) {
        // level 컬럼을 포함하여 저장합니다.
        String sql = "INSERT INTO game_records (username, score, level, play_time) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, record.getUser().getUsername());
            pstmt.setInt(2, record.getScore());
            pstmt.setInt(3, record.getLevel()); // Record 객체에서 가져온 레벨 저장

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("기록 저장 중 오류 발생!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 전체 기록 불러오기 (SELECT)
     * level 컬럼을 조회하고 Record 객체 생성 시 생성자에 넘겨줍니다.
     */
    // RecordDAO.java 내부
    public List<Record> getAllRecords() {
        List<Record> list = new ArrayList<>();
        // r.level이 빠져있지는 않은지 꼭 확인하세요!
        String sql = "SELECT r.record_id, r.username, r.score, r.play_time, r.level, u.nickname, u.gender " +
                "FROM game_records r " +
                "JOIN users u ON r.username = u.username " +
                "ORDER BY r.play_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("nickname"));
                user.setGender(rs.getString("gender"));

                // 여기서 rs.getInt("level")을 마지막에 꼭 넣어주어야 합니다.
                Record record = new Record(
                        rs.getInt("record_id"),
                        user,
                        rs.getInt("score"),
                        rs.getString("play_time"),
                        rs.getInt("level") // 이 부분이 생성자의 5번째 인자로 들어갑니다.
                );
                list.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}