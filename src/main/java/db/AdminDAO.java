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
     //모든 음식 내역 
      public List<FoodDTO> showFood(){
        List<FoodDTO> list = new ArrayList<FoodDTO>();
        try(Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("select * from food")){
            try(ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                   FoodDTO food = new FoodDTO();
                   food.setFood_idx(rs.getInt("food_idx"));
                   food.setFood_name(rs.getString("fooD_name"));
                   food.setFood_price(rs.getInt("food_price"));
                   food.setFood_stock(rs.getInt("fooD_stock"));
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
    //음식 추가
    public void insertFood(FoodDTO f){
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("select food_idx from food where food_name = ? and food_price = ?")){
            pstmt.setString(1, f.getFood_name());
            pstmt.setInt(2, f.getFood_price());
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    int duplication = rs.getInt("food_idx");
                   try(PreparedStatement pstmtUpdate = conn.prepareStatement("update food set food_stock = food_stock + ? where food_idx = ?") ){
                       pstmtUpdate.setInt(1, f.getFood_stock());
                       pstmtUpdate.setInt(2, duplication);
                       pstmtUpdate.executeUpdate();
                   } catch (Exception e) {
                    e.printStackTrace();
                   }
                }
                else{
                    try (PreparedStatement pstmtInsert = conn.prepareStatement("insert into food(food_idx, food_name, food_price, food_stock) values(?,?,?,?)")) {
                            pstmtInsert.setInt(1, f.getFood_idx());
                            pstmtInsert.setString(2, f.getFood_name());
                            pstmtInsert.setInt(3, f.getFood_price());
                            pstmtInsert.setInt(4, f.getFood_stock());
                            pstmtInsert.executeUpdate();
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
               
                
            }
        
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("DB 처리 중 오류 발생: " + e.getMessage());
        
    }
     
    } 
    //음식 삭제

    
    public void deletefood(FoodDTO f){
        try(Connection conn = getConnection();
            PreparedStatement psmts = conn.prepareStatement("delete from food where food_idx = ? ")) {
            psmts.setInt(1, f.getFood_idx());
            psmts.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 음식 변경
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
    public List<OrdersDTO> showOrder(){ //모든 주문내역 
        List<OrdersDTO> list = new ArrayList<OrdersDTO>();
        try(Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("select m.mem_name, f.food_name, f.food_stock, o.seat_idx, o.od_idx\r\n" + //
                        "from orders o\r\n" + //
                        "join food f\r\n" + //
                        "on o.food_idx = f.food_idx\r\n" + //
                        "join member m\r\n" + //
                        "on o.mem_idx = m.mem_idx"  //
                        )) {
            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    OrdersDTO order = new OrdersDTO();
                    order.setOd_idx(rs.getInt("od_idx"));
                    order.setMem_name(rs.getString("mem_name"));
                    order.setFood_name(rs.getString("food_name"));
                    order.setFood_stock(rs.getInt("food_stock"));
                    order.setSeat_idx(rs.getInt("seat_idx"));
                    list.add(order);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
                        
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public void deleteOrder(int od_idx){ //주문삭제
        try (Connection conn = getConnection();
          PreparedStatement pstmt = conn.prepareStatement("delete from orders where od_idx = ?")){
            pstmt.setInt(1, od_idx);
            pstmt.executeUpdate();
            
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
       public List<TimeDTO> getAllTime(){ //모든 시간과 가격
        List<TimeDTO> list = new ArrayList<TimeDTO>();
        try(Connection conn = getConnection(); 
       PreparedStatement pstmt = conn.prepareStatement("select * from time_menu order by hour ASC")) {
            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TimeDTO time = new TimeDTO();
                    time.setHour(rs.getInt("hour"));
                    time.setPrice(rs.getInt("price"));
                    list.add(time);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }//가격 변경
    public void updateTimePrice(int hour, int newprice){
        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement("update time_menu SET price = ? where hour = ?")){
            pstmt.setInt(1, newprice);
            pstmt.setInt(2, hour);
            pstmt.executeUpdate();
        } catch (Exception e) {
           e.printStackTrace();
        } 
    } 
    // 시간 삭제
    public void deleteTime(int hour){
        try(Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("delete from time_menu where hour = ?")){
            pstmt.setInt(1, hour);
            pstmt.executeUpdate();
           
            
        } catch (Exception e) {
            e.printStackTrace();
           
        } 
    }  //시간 가격 추가
    public void inserthour(int hour, int price){
   try(Connection conn = getConnection();
    PreparedStatement pstmt = conn.prepareStatement("insert into time_menu(hour, price) values(?,?)")) {
        pstmt.setInt(1, hour);
        pstmt.setInt(2, price);
        pstmt.executeUpdate();
    
   } catch (Exception e) {
    e.printStackTrace();
   }//시간을 추가
    
}


}
