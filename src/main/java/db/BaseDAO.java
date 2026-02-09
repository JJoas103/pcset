package db;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class BaseDAO {
    protected String url = "jdbc:mysql://localhost:3306/yuls";
    protected String user = "root";
    protected String pass = "sukyum1003.";

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection =  DriverManager.getConnection(url, user, pass);
        return connection;
    }
}
