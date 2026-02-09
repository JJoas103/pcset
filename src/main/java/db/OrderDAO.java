package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import vo.OrdersDTO;

public class OrderDAO {
    String url = "jdbc:mysql://localhost:3306/yuls";
    String user = "root";
    String pass = "sukyum1003.";

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection =  DriverManager.getConnection(url, user, pass);
        return connection;
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
}