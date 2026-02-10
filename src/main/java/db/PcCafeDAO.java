package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import vo.MemberDTO;
import vo.OrdersDTO;
import vo.FoodDTO;

public class PcCafeDAO extends BaseDAO{

    // 1. 회원 정보 가져오기
    public MemberDTO getMember(int mem_idx) {
        String sql = "SELECT * FROM member WHERE mem_idx = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, mem_idx);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                MemberDTO member = new MemberDTO();
                member.setMem_idx(rs.getInt("mem_idx"));
                member.setMem_id(rs.getString("mem_id"));
                member.setMem_name(rs.getString("mem_name"));
                member.setMem_time(rs.getInt("mem_time"));
                member.setMem_money(rs.getInt("mem_money"));
                return member;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 2. 음식 전체 목록 가져오기
    public List<FoodDTO> getAllFoods() {
        List<FoodDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM food";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(new FoodDTO(
                    rs.getInt("food_idx"),
                    rs.getString("food_name"),
                    rs.getInt("food_price"),
                    rs.getInt("food_stock")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. 시간 충전
    public boolean chargeTime(int mem_idx, int addTime, int cost) {
        // 1. 현재 잔액 확인
        String checkBalanceSql = "SELECT mem_money FROM member WHERE mem_idx = ?";
        String updateSql = "UPDATE member SET mem_money = mem_money - ?, mem_time = mem_time + ? WHERE mem_idx = ?";
        
        try (Connection conn = getConnection()) {
            // Check balance first
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkBalanceSql)) {
                checkPstmt.setInt(1, mem_idx);
                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if (rs.next()) {
                        int currentMoney = rs.getInt("mem_money");
                        if (currentMoney < cost) {
                            return false; // 잔액 부족
                        }
                    } else {
                        return false; // 회원 정보 없음
                    }
                }
            }

            // If balance is sufficient, proceed with update
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, cost);
                updatePstmt.setInt(2, addTime);
                updatePstmt.setInt(3, mem_idx);
                return updatePstmt.executeUpdate() > 0;
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
    
    // 4. 음식 주문 (트랜잭션 적용)
    public String orderFood(int mem_Idx, int food_idx, int qty, int seat_idx) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // (1) 잔액 및 재고 확인
            //입력한회원의 잔액 가져오기
            String memSql = "SELECT m.mem_money FROM member m WHERE m.mem_idx = ?";
            PreparedStatement pstmtMem = conn.prepareStatement(memSql);
            pstmtMem.setInt(1, mem_Idx);
            ResultSet rsMem = pstmtMem.executeQuery();
            
            //입력한 음식이름으로 음식가격과 음식수량 가져오기
            String foodSql = "SELECT f.food_price, f.food_stock FROM food f WHERE f.food_idx = ?";
            PreparedStatement pstmtFood = conn.prepareStatement(foodSql);
            pstmtFood.setInt(1, food_idx);
            ResultSet rsFood = pstmtFood.executeQuery();

            if (!rsMem.next() || !rsFood.next()) return "정보 조회 오류";

            int balance = rsMem.getInt("mem_money");    //보유금액
            int price = rsFood.getInt("food_price");    //음식 가격
            int food_stock = rsFood.getInt("food_stock");    //보유 재고 수량
            int totalPrice = price * qty; //음식금액 = 음식 가격 * 주문 수량

            if (food_stock < qty) return "재고 부족";
            if (balance < totalPrice) return "잔액 부족";

            
            // (2) 업데이트 실행 잔액감소
            String updateMem = "update member SET mem_money = mem_money - ? WHERE mem_idx = ?";
            PreparedStatement upMemStmt = conn.prepareStatement(updateMem);
            upMemStmt.setInt(1, totalPrice);
            upMemStmt.setInt(2, mem_Idx);
            upMemStmt.executeUpdate();
            //수량감소
            String updateFood = "update food set food_stock = food_stock - ? where food_idx = ?";
            PreparedStatement upFoodStmt = conn.prepareStatement(updateFood);
            upFoodStmt.setInt(1, qty);
            upFoodStmt.setInt(2, food_idx);
            upFoodStmt.executeUpdate();
            String insertOrder = "insert into orders(mem_idx, seat_idx, food_idx, od_qty) values (?, ?, ?, ?)";
            PreparedStatement inOrderStmt = conn.prepareStatement(insertOrder);
            inOrderStmt.setInt(1, mem_Idx);
            inOrderStmt.setInt(2, seat_idx);
            inOrderStmt.setInt(3, food_idx);
            inOrderStmt.setInt(4, qty);
            inOrderStmt.executeUpdate();
            conn.commit(); // 커밋
            return "SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return "DB 오류";
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }
    //모든 주문내역     
    public List<OrdersDTO> showOrderIdx(){ 
            List<OrdersDTO> listIdx = new ArrayList<OrdersDTO>();
            try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select mem_idx, food_idx, seat_idx\r\n" + //
                            "from orders o\r\n" + //
                            "join food f\r\n" + //
                            "on o.food_idx = f.food_idx\r\n" + //
                            "join member m\r\n" + //
                            "on o.mem_idx = m.mem_idx"  //
                            )) {
                try (ResultSet rs = pstmt.executeQuery()){
                    while (rs.next()) {
                        OrdersDTO order = new OrdersDTO();
                        order.setMem_idx(rs.getInt("mem_idx"));
                        order.setFood_idx(rs.getInt("food_idx"));
                        order.setOd_idx(rs.getInt("od_idx"));
                        order.setMem_name(rs.getString("mem_name"));
                        order.setFood_name(rs.getString("food_name"));
                        order.setFood_stock(rs.getInt("food_stock"));
                        order.setSeat_idx(rs.getInt("seat_idx"));
                        listIdx.add(order);
                    }
                    
                } catch (Exception e) {
                e.printStackTrace();
                }
                        
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            return listIdx;
        }
}