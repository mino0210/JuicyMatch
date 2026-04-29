package cardGame.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties props = new Properties();

    static {
        // FileInputStream 대신 getResourceAsStream을 사용하여 빌드 경로 내의 파일을 안전하게 읽음
        // Use getResourceAsStream instead of FileInputStream to safely read files in build path
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                System.err.println("db.properties 파일을 찾을 수 없습니다! src 폴더 바로 아래에 있는지 확인하세요.");
            } else {
                props.load(is);
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("DB 설정 로드 성공");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("DB 설정 로드 실패: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            // properties 파일의 키 이름(url, username, password)과 일치하도록 수정
            // Match key names (url, username, password) with properties file
            String url = props.getProperty("url");
            String user = props.getProperty("username");
            String password = props.getProperty("password");

            if (url == null) {
                System.err.println("DB 연결 실패: properties에서 'url' 설정을 찾을 수 없습니다.");
                return null;
            }

            return DriverManager.getConnection(url, user, password);
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