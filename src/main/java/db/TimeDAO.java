package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import vo.TimeDTO;

public class TimeDAO extends BaseDAO {
    //시간 메뉴 리스트
    public List<TimeDTO> getAllTime(){
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
    }
    //가격 변경
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
    }  
    //시간 가격 추가
    public boolean inserthour(int hour, int price){
        if (isTimeOptionExist(hour)) {
            System.out.println("TimeDAO: Cannot insert, hour " + hour + " already exists.");
            return false; // Already exists
        }
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement("insert into time_menu(hour, price) values(?,?)")) {
                pstmt.setInt(1, hour);
                pstmt.setInt(2, price);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean isTimeOptionExist(int hour) {
        String sql = "SELECT 1 FROM time_menu WHERE hour = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hour);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
