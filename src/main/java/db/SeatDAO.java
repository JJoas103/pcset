package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import vo.SeatDTO;

public class SeatDAO extends BaseDAO{
    
    public ArrayList<SeatDTO> getAllSeats() {
        ArrayList<SeatDTO> list = new ArrayList<>();
        String sql = "select s.*, m.mem_name, m.mem_age as member_mem_age from seat s " +
                     "left join member m on s.mem_idx = m.mem_idx " + 
                     "order by seat_idx";

        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);) {
            try (ResultSet rs = pstmt.executeQuery();){
                while(rs.next()) {
                    SeatDTO seats = new SeatDTO();
                    seats.setSeatIdx(rs.getInt("seat_idx"));
                    seats.setMemIdx(rs.getInt("mem_idx"));
                    seats.setStatus(rs.getInt("status"));
                    seats.setMemName(rs.getString("mem_name"));
                    seats.setMemAge(rs.getInt("member_mem_age"));

                    list.add(seats);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    //좌석 찾기
    public int findSeat(int memIdx){
        int seatIdx = 0;
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement 
            = connection.prepareStatement("select seat_idx from seat where mem_idx = ? and status = 1");){
                preparedStatement.setInt(1, memIdx);
                try (ResultSet rs = preparedStatement.executeQuery()){
                    if(rs.next()){
                        seatIdx = rs.getInt("seat_idx");
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return seatIdx; // 없으면 0 
    }

    //좌석 선택 앉기: 빈좌석(status == 0 일때만 성공)
    public boolean selectSeat(int seatIdx, int memIdx){
    boolean result = false;
    // 쿼리 설명: member 테이블에서 해당 memIdx의 이름과 나이를 가져와서 seat 테이블에 한 번에 업데이트함
    String sql = "UPDATE seat s, member m " +
                 "SET s.mem_idx = m.mem_idx, " +
                 "    s.mem_name = m.mem_name, " +
                 "    s.mem_age = m.mem_age, " +
                 "    s.status = 1 " +
                 "WHERE s.seat_idx = ? AND s.status = 0 AND m.mem_idx = ?";

    try (Connection connection = getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setInt(1, seatIdx);
            preparedStatement.setInt(2, memIdx);
            
            int count = preparedStatement.executeUpdate();
            if (count == 1){
                result = true;
            }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
    return result;
}
    public boolean moveSeat(int memIdx, int fromSeatIdx, int toSeatIdx) {
    String clearSql = "UPDATE seat SET mem_idx = NULL, mem_name = NULL, mem_age = NULL, status = 0 " +
                      "WHERE seat_idx = ? AND mem_idx = ?";
    
    String occupySql = "UPDATE seat s, member m " +
                       "SET s.mem_idx = m.mem_idx, s.mem_name = m.mem_name, s.mem_age = m.mem_age, s.status = 1 " +
                       "WHERE s.seat_idx = ? AND s.status = 0 AND m.mem_idx = ?";

    try (Connection connection = getConnection()) {
        connection.setAutoCommit(false); // 트랜잭션 시작

        try {
            // 1. 새 좌석 점유 시도 (이미 누가 앉았다면 여기서 바로 실패해야 함)
            try (PreparedStatement pstmt1 = connection.prepareStatement(occupySql)) {
                pstmt1.setInt(1, toSeatIdx);
                pstmt1.setInt(2, memIdx);
                if (pstmt1.executeUpdate() != 1) {
                    connection.rollback();
                    return false;
                }
            }

            // 2. 기존 좌석 비우기
            try (PreparedStatement pstmt2 = connection.prepareStatement(clearSql)) {
                pstmt2.setInt(1, fromSeatIdx);
                pstmt2.setInt(2, memIdx);
                if (pstmt2.executeUpdate() != 1) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
    public boolean leaveSeat(int memIdx) { // 내 자리 클릭 시 퇴실, 빈좌석으로 변경
        String sql =
                "UPDATE seat " +
                "SET mem_idx = NULL, " +
                "    mem_name = NULL, " +
                "    mem_age = NULL, " +
                "    status = 0 " +
                "WHERE mem_idx = ? AND status = 1";
        try (
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, memIdx);
            return preparedStatement.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean setRepairStatus(int seatIdx, int status){
        String sql = "update seat set status = ?, mem_idx = null, mem_name = null, mem_age = null where seat_idx = ?";
        try(Connection con = getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setInt(1, status);
                preparedStatement.setInt(2, seatIdx);
                return preparedStatement.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }return false;
    }
}
