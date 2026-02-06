package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import vo.LogDTO;

public class LogDAO {

    String url = "jdbc:mysql://localhost:3306/yuls";
    String user = "root";
    String pass = "sukyum1003.";

    public Connection getConnection() throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, pass);
        return connection;
    }
    // 날짜별 상세 내역 가져오기
    public ArrayList<LogDTO> getLogList(String date) {
        ArrayList<LogDTO> list = new ArrayList<>();
        
        String sql = "select l.log_idx, l.log_type, l.log_amount, l.log_date, m.mem_name " +
                     "from log l " +
                     "join member m ON l.mem_idx = m.mem_idx " + 
                     "where l.log_date LIKE ? " +
                     "order by l.log_date asc"; // 최신순 정렬

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, date + "%"); // ex) 2026-01%

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LogDTO vo = new LogDTO();
                    
                    // 기존 데이터 매핑
                    vo.setLogIdx(rs.getInt("log_idx"));
                    vo.setLogType(rs.getInt("log_type"));
                    vo.setLogAmount(rs.getInt("log_amount"));
                    vo.setLogDate(rs.getTimestamp("log_date"));
                    
                    // [중요] 조인해서 가져온 이름을 VO에 세팅
                    String name = (rs.getString("mem_name"));
                    if(name == null) {
                        vo.setMemName("탈퇴한 회원");
                    } else {
                        vo.setMemName(name);
                    }
                    
                    list.add(vo);
                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }
    public int getIncome(String date) {
        int total = 0;
        /*
        String sql = "select sum(log_amount) " +
                      "from log " + 
                      "where date(log_date) = curdate()";
                      */
        
        String sql = "select sum(log_amount) from log where log_date like ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, date+"%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    if(rs.next()) {
                        total = rs.getInt(1);
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        } return total;
    }
    // 로그 추가
    public void insertLog(int memIdx, int logType, int logAmount) {
        String sql = "insert into log(mem_idx, log_type, log_amount) values(?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memIdx);
            pstmt.setInt(2, logType);
            pstmt.setInt(3, logAmount);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
