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
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement
             = connection.prepareStatement("update seat set mem_idx = ?, status = 1 " + "where seat_idx = ? and status = 0");) {
                preparedStatement.setInt(1, memIdx);
                preparedStatement.setInt(2, seatIdx);
                
                int count = preparedStatement.executeUpdate();
                if (count == 1){
                    result = true;
                }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    //좌석이동
    public boolean moveSeat(int memIdx, int fromSeatIdx, int toSeatIdx) {// 2개의 시트idx

        boolean result = false;

        try (Connection connection = getConnection()) {

            connection.setAutoCommit(false);

            // 1) 기존 좌석 비우기 (내가 앉아있는 자리만 비워야 함)
            try (PreparedStatement pstmt1 =
                    connection.prepareStatement(
                            "update seat set mem_idx = null, status = 0 " +
                            "where seat_idx = ? and mem_idx = ? and status = 1"
                            //멤버idx를 널값, 상태를 0 업데이트 후 내가 선택한 좌석의 상태를 1로
                    )) {

                pstmt1.setInt(1, fromSeatIdx);
                pstmt1.setInt(2, memIdx);

                int cnt1 = pstmt1.executeUpdate();
                if (cnt1 != 1) {                 // 비우기 실패면 전체 취소
                    connection.rollback();
                    return false;
                }
            }

            // 2) 새 좌석 채우기 (빈 좌석일 때만 성공)
            try (PreparedStatement pstmt2 =
                    connection.prepareStatement(
                            "update seat set mem_idx = ?, status = 1 " +
                            "where seat_idx = ? and status = 0" //내가 선택한 좌석을 빈좌석일때, 선택한 좌석을 사용중으로 변경 
                    )) {

                pstmt2.setInt(1, memIdx);
                pstmt2.setInt(2, toSeatIdx);

                int cnt2 = pstmt2.executeUpdate();
                if (cnt2 != 1) {                 // 채우기 실패면 전체 취소
                    connection.rollback();
                    return false;
                }
            }

            connection.commit(); // 둘 다 성공해야 커밋 전체 실행
            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    public boolean leaveSeat(int memIdx){// 내자리 클릭시 퇴실, 빈자리로 1
        String sql = "update seat set mem_idx = null, status = 0 where mem_idx =? and status =1";
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, memIdx);
                return preparedStatement.executeUpdate() == 1;
            
        } catch (Exception e) {
            e.printStackTrace();
        } return false;

    }
}
