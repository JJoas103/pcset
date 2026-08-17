package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public abstract class BaseDAO {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = BaseDAO.class.getResourceAsStream("/db.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("DB 설정 파일을 읽을 수 없습니다", e);
        }
    }

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.pass"));
    }
}
