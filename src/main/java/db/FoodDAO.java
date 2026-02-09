package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import vo.FoodDTO;

public class FoodDAO extends BaseDAO {
    //음식 list 보여주기
    public List<FoodDTO> showFood(){
        List<FoodDTO> list = new ArrayList<FoodDTO>();
        try(Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("select * from food")){
            try(ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                   FoodDTO food = new FoodDTO();
                   food.setFood_idx(rs.getInt("food_idx"));
                   food.setFood_name(rs.getString("food_name"));
                   food.setFood_price(rs.getInt("food_price"));
                   food.setFood_stock(rs.getInt("food_stock"));
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
            psmts.setString(1, f.getFood_name());
            psmts.setInt(2, f.getFood_price());
            psmts.setInt(3, f.getFood_stock());
            psmts.setInt(4, f.getFood_idx());
            psmts.executeUpdate();
            
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
}
