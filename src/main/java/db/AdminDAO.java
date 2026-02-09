package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import vo.FoodDTO;
import vo.OrdersDTO;
import vo.TimeDTO;

public class AdminDAO {
     String url = "jdbc:mysql://localhost:3306/yuls";
    String user = "root";
    String pass = "sukyum1003.";

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection =  DriverManager.getConnection(url, user, pass);
        return connection;
    }








    // 시간 삭제
//시간 가격 추가



}
