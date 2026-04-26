package cardGame.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties props = new Properties();

    // 클래스 로드 시 설정을 한 번 읽어옵니다.
    static {
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            props.load(fis);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("DB 설정 로드 실패: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            );
        } catch (SQLException e) {
            System.err.println("DB 연결 실패: " + e.getMessage());
            return null;
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}