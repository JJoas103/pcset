package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import vo.FoodDTO;

public class FoodDAO {
    String url = "jdbc:mysql://localhost:3306/yuls";
    String user = "root";
    String pass = "sukyum1003.";

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection =  DriverManager.getConnection(url, user, pass);
        return connection;
    }
    public List<FoodDTO> showFood(){
        List<FoodDTO> list = new ArrayList<FoodDTO>();
        try(Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("select * from food")){
            try(ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                   FoodDTO food = new FoodDTO();
                   food.setFood_idx(rs.getInt("food_idx"));
                   food.setFood_name(rs.getString("foo_name"));
                   food.setFood_price(rs.getInt("food_price"));
                   food.setFood_stock(rs.getInt("foo_stock"));
                   list.add(food);
                }
            } catch (Exception e) {
               e.printStackTrace();
            }
            
        } catch (Exception e) {
           e.printStackTrace();
        }
        return list;
    }

    public void insertFood(FoodDTO f){
     try(Connection conn = getConnection();
       PreparedStatement pstmt = conn.prepareStatement("insert into food(food_name, food_price, food_stock) values (?, ?, ?)")) {
         pstmt.setString(1, f.getFood_name());
         pstmt.setInt(2, f.getFood_price());
         pstmt.setInt(3, f.getFood_stock());
         pstmt.executeUpdate();
     } catch (Exception e) {
        e.printStackTrace();
     }
    }
    public void deletefood(FoodDTO f){
        try(Connection conn = getConnection();
            PreparedStatement psmts = conn.prepareStatement("delete from food where food_idx = ? ")) {
            psmts.setInt(1, f.getFood_idx());
            psmts.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateFood(FoodDTO f){
        try(Connection conn = getConnection();
           PreparedStatement psmts = conn.prepareStatement("update food set food_name = ?, food_price = ?, food_stock = ? where food_idx = ?")){
            psmts.setString(1, "food_name");
            psmts.setString(2, "food_price");
            psmts.setString(3, "food_stock");
            psmts.setString(4, "food idx");
            psmts.executeUpdate();
            
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
}
