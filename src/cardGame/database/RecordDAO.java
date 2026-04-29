package cardGame.database;

import cardGame.entity.Record;
import cardGame.entity.User;
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class RecordDAO {

    /**
     * 1. 게임 결과 저장 (INSERT)
     */
    public boolean insertRecord(Record record) {
        String sql = "INSERT INTO game_records (username, score, level, play_time) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, record.getUser().getUsername());
            pstmt.setInt(2, record.getScore());
            pstmt.setInt(3, record.getLevel());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("기록 저장 중 오류 발생!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 2. 특정 유저의 레벨별 통계 (대시보드 확장 섹션용)
     * 시간대 보정을 위해 Calendar.getInstance(TimeZone)를 활용할 수 있도록 구성했습니다.
     */
    public List<Map<String, Object>> getUserStatistics(String username) {
        List<Map<String, Object>> statsList = new ArrayList<>();
        String sql = "SELECT level, SUM(score) as total_lv_score, " +
                "MAX(score) as max_lv_score, MAX(play_time) as last_play " +
                "FROM game_records WHERE username = ? " +
                "GROUP BY level ORDER BY level ASC";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return statsList;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("level", rs.getInt("level"));
                        row.put("totalScore", rs.getInt("total_lv_score"));
                        row.put("maxScore", rs.getInt("max_lv_score"));

                        Timestamp ts = rs.getTimestamp("last_play");
                        row.put("lastPlay", (ts != null) ? sdf.format(ts) : "-");

                        statsList.add(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statsList;
    }

    /**
     * 3. 전체 유저별 '총점 합계' 기준 랭킹 리스트
     * 3. Ranking list per user based on total score sum
     */
    public List<Map<String, Object>> getGlobalRankings() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT r.username, u.nickname, SUM(r.score) as total_sum, MAX(r.score) as best_score " +
                "FROM game_records r " +
                "JOIN users u ON r.username = u.username " +
                "GROUP BY r.username ORDER BY total_sum DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("username", rs.getString("username"));
                map.put("nickname", rs.getString("nickname"));
                map.put("totalSum", rs.getInt("total_sum"));
                map.put("bestScore", rs.getInt("best_score"));
                list.add(map);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * 4. 상단 대시보드용 요약 정보 (내 순위 계산 로직 추가)
     * 4. Summary info for top dashboard (with my rank calculation logic)
     */
    public Map<String, Object> getDashboardSummary(String targetUsername) {
        Map<String, Object> summary = new HashMap<>();

        // 전체 유저 수, 내 총점, 내 최고점 쿼리
        // Query total users, my total score, my best score
        String infoSql = "SELECT " +
                "(SELECT COUNT(DISTINCT username) FROM game_records) as total_users, " +
                "IFNULL(SUM(score), 0) as my_total, " +
                "IFNULL(MAX(score), 0) as my_best " +
                "FROM game_records WHERE username = ?";

        // 내 순위 계산 쿼리 (총점 합계 기준)
        // Query my rank (based on total score sum)
        String rankSql = "SELECT rank_no FROM (" +
                "  SELECT username, RANK() OVER (ORDER BY SUM(score) DESC) as rank_no " +
                "  FROM game_records GROUP BY username" +
                ") as ranking WHERE username = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // 기본 정보 조회
            // Query basic info
            try (PreparedStatement pstmt = conn.prepareStatement(infoSql)) {
                pstmt.setString(1, targetUsername);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        summary.put("totalUsers", rs.getInt("total_users"));
                        summary.put("myTotal", rs.getInt("my_total"));
                        summary.put("myBest", rs.getInt("my_best"));
                    }
                }
            }
            // 순위 조회
            // Query rank
            try (PreparedStatement pstmt = conn.prepareStatement(rankSql)) {
                pstmt.setString(1, targetUsername);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        summary.put("myRank", rs.getInt("rank_no"));
                    } else {
                        summary.put("myRank", "-"); // 기록이 없는 경우
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return summary;
    }

    /**
     * 5. 기존 전체 기록 리스트 (필요 시 유지)
     * Get all records / 전체 기록 조회
     */
    public List<Record> getAllRecords() {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT r.record_id, r.username, r.score, r.play_time, r.level, u.nickname, u.gender " +
                "FROM game_records r JOIN users u ON r.username = u.username " +
                "ORDER BY r.play_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("nickname"));
                user.setGender(rs.getString("gender"));

                list.add(new Record(
                        rs.getInt("record_id"), user, rs.getInt("score"),
                        rs.getString("play_time"), rs.getInt("level")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    /**
     * 7. 특정 사용자의 레벨별 통계 (총점, 최고점, 횟수)
     * Get level stats with play count / 레벨별 통계 + 플레이 횟수
     */
    public Map<Integer, int[]> getUserLevelStats(String username) {
        // Map<level, [totalScore, bestScore, playCount]>
        Map<Integer, int[]> statsMap = new HashMap<>();
        // 기본값: 0
        // Default value: 0
        statsMap.put(1, new int[]{0, 0, 0});
        statsMap.put(2, new int[]{0, 0, 0});
        statsMap.put(3, new int[]{0, 0, 0});
        
        String sql = "SELECT level, SUM(score) as total, MAX(score) as best, COUNT(*) as cnt " +
                "FROM game_records WHERE username = ? GROUP BY level";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int level = rs.getInt("level");
                    int total = rs.getInt("total");
                    int best = rs.getInt("best");
                    int cnt = rs.getInt("cnt");
                    statsMap.put(level, new int[]{total, best, cnt});
                }
            }
        } catch (SQLException e) {
            System.err.println("レベル統計の照会中にエラーが発生しました: " + username);
            e.printStackTrace();
        }
        return statsMap;
    }
    
    /**
     * 6. 특정 사용자의 모든 기록 조회
     * Get records by user ID / 사용자 ID로 기록 조회
     */
    public List<Record> getRecordsByUserId(String userId) {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT r.record_id, r.username, r.score, r.play_time, r.level, u.nickname, u.gender " +
                "FROM game_records r JOIN users u ON r.username = u.username " +
                "WHERE r.username = ? " +
                "ORDER BY r.play_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUsername(rs.getString("username"));
                    user.setName(rs.getString("nickname"));
                    user.setGender(rs.getString("gender"));

                    list.add(new Record(
                            rs.getInt("record_id"), user, rs.getInt("score"),
                            rs.getString("play_time"), rs.getInt("level")
                    ));
                }
            }
        } catch (SQLException e) { 
            System.err.println("사용자 기록 조회 중 오류 발생: " + userId);
            e.printStackTrace(); 
        }
        return list;
    }
}