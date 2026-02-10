package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import vo.MemberDTO;
import org.mindrot.jbcrypt.BCrypt;

public class MemberDAO extends BaseDAO{
    // 일반 회원가입
    public void insertMember(MemberDTO joinMember){
        try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("insert into member(mem_id, mem_pass, mem_name, mem_age) values(?,?,?,?)") ){
           pstmt.setString(1, joinMember.getMem_id());
           pstmt.setString(2, joinMember.getMem_pass());
           pstmt.setString(3, joinMember.getMem_name());
           pstmt.setInt(4, joinMember.getMem_age());
           pstmt.executeUpdate();
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    //로그인상태
    public MemberDTO loginMember(String inputId, String inputPass){
        MemberDTO loginMember = null;
        try(Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("select * from member where mem_id = ?")) {
                pstmt.setString(1, inputId);
                try(ResultSet rs = pstmt.executeQuery()) {
                    if(rs.next()){
                        String hashedPasswordFromDB = rs.getString("mem_pass");
                        if(BCrypt.checkpw(inputPass, hashedPasswordFromDB)) {
                            loginMember = new MemberDTO();
                            loginMember.setMem_idx(rs.getInt("mem_idx"));
                            loginMember.setMem_id(rs.getString("mem_id"));
                            loginMember.setMem_pass(hashedPasswordFromDB); // 복호화된 비밀번호
                            loginMember.setMem_name(rs.getString("mem_name"));
                            loginMember.setMem_time(rs.getInt("mem_time"));
                            loginMember.setMem_money(rs.getInt("mem_money"));
                            loginMember.setMem_admin(rs.getInt("mem_admin"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginMember;
    }

    //중복확인
    public boolean checkId(String inputId){
        try(Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("select * from member where mem_id = ?");) {
            pstmt.setString(1, inputId);
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    return true;
                }//중복된 ID 존재
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //사용자 금액충전 
    public boolean chargeMoney(MemberDTO loginMember, int payMoney) {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement 
                = connection.prepareStatement("update member set mem_money = mem_money + ? where mem_idx = ?")){
                    preparedStatement.setInt(1, payMoney);
                    preparedStatement.setInt(2, loginMember.getMem_idx());
                    int rowsAffected = preparedStatement.executeUpdate();
                    return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ArrayList<MemberDTO> getAllMembers() {
        ArrayList<MemberDTO> list = new ArrayList<>();
        String sql = "select * from member";

        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    MemberDTO members = new MemberDTO();
                    members.setMem_idx(rs.getInt("mem_idx"));
                    members.setMem_id(rs.getString("mem_id"));
                    members.setMem_name(rs.getString("mem_name"));
                    members.setMem_time(rs.getInt("mem_time"));
                    members.setMem_age(rs.getInt("mem_age"));
                    members.setMem_money(rs.getInt("mem_money"));

                    list.add(members);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return list;
    }
    public int updateMember(MemberDTO members) {
        String sql = "update member " +
        "set mem_name = ?, mem_time = ?, mem_money = ?, mem_age = ? " +
        "where mem_idx = ?";

        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, members.getMem_name());
                pstmt.setInt(2, members.getMem_time());
                pstmt.setInt(3, members.getMem_money());
                pstmt.setInt(4, members.getMem_age());
                pstmt.setInt(5, members.getMem_idx());
                
                return pstmt.executeUpdate();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        return 0;
    }
    public int updatePass(int idx, String pass) {
        String sql = "update member set mem_pass = ? where mem_idx = ?";
        
        String hashedPass = BCrypt.hashpw(pass, BCrypt.gensalt());
        
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, hashedPass);
                pstmt.setInt(2, idx);

                return pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return 0;
    }
    public int deleteMember(int idx) {
        String sql1 = "update orders set mem_idx = NULL where mem_idx = ?";
            // 주문 기록을 남겨야 함
        String sql2 = "update log set mem_idx = NULL where mem_idx = ?";
            // 매출 통계를 위해서
        String sql3 = "delete from member where mem_idx = ?";
            // 완성 후 삭제
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                    pstmt.setInt(1, idx);
                    pstmt.executeUpdate();
                }
                try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                    pstmt.setInt(1, idx);
                    pstmt.executeUpdate();
                }
                int result;
                try (PreparedStatement pstmt = conn.prepareStatement(sql3)) {
                    pstmt.setInt(1, idx);
                    result = pstmt.executeUpdate();
                }
                conn.commit(); // 성공
                return result;

            } catch (Exception e) {
                // 실패하면 롤백
                e.printStackTrace();
                conn.rollback();
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // memIdx 존재 여부 확인
    public boolean isMemberExist(int memIdx) {
        String sql = "select 1 from member where mem_idx = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memIdx);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getMemberName(int memIdx) {
        String memName = null;
        String sql = "select mem_name from member where mem_idx = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memIdx);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    memName = rs.getString("mem_name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return memName;
    }
}
