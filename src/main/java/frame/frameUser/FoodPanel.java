package frame.frameUser;                                                                                                                            
                                                                                                                                       
import javax.swing.*;                                                                                                                  
import javax.swing.table.DefaultTableModel;

import db.SeatDAO; 
import db.LogDAO; 

import java.awt.*;
import java.util.List;

import vo.FoodDTO;
import vo.MemberDTO;
import vo.OrdersDTO;

public class FoodPanel extends JPanel {
    UserView mainFrame; 
    MemberDTO loginMember;
    DefaultTableModel tableModel;
    JTable foodTable;
    private SeatDAO seatDAO = new SeatDAO(); 
    private LogDAO logDAO = new LogDAO();

    public FoodPanel(UserView mainFrame, MemberDTO loginMember) {
        this.mainFrame = mainFrame; 
        this.loginMember = loginMember;
        setLayout(new BorderLayout());
        // 테이블 구성
        String[] colNames = {"ID", "음식 이름", "가격", "남은 수량"};
        tableModel = new DefaultTableModel(colNames, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        foodTable = new JTable(tableModel);
        add(new JScrollPane(foodTable), BorderLayout.CENTER);

        // 주문 버튼
        JButton btnOrder = new JButton("주문하기");
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnOrder);
        add(btnPanel, BorderLayout.SOUTH);

        btnOrder.addActionListener(e -> {
            int row = foodTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "음식을 선택하세요.");
                return;
            }

            int foodId = (int) tableModel.getValueAt(row, 0);
            String foodName = (String) tableModel.getValueAt(row, 1);
            int foodPrice = (int) tableModel.getValueAt(row, 2); // 음식 가격 가져오기

            String input = JOptionPane.showInputDialog(this, foodName + " 몇 개 주문하시겠습니까?");
            if (input == null || input.isEmpty()) return;

            try {
                int qty = Integer.parseInt(input);
                if (qty <= 0) throw new NumberFormatException();

                // seat_idx 가져오기
                int seatIdx = seatDAO.findSeat(loginMember.getMem_idx());
                if (seatIdx == 0) {
                    JOptionPane.showMessageDialog(this, "좌석을 선택해야 음식을 주문할 수 있습니다.");
                    return;
                }

                // OrdersDTO 생성 및 seat_idx 설정
                OrdersDTO orderInfo = new OrdersDTO();
                orderInfo.setSeat_idx(seatIdx);

                String result = mainFrame.dao.orderFood(loginMember.getMem_idx(), foodId, qty, seatIdx);

                if (result.equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(this, "주문 성공!");
                    logDAO.insertLog(loginMember.getMem_idx(), 2, foodPrice * qty); // logType: 2 -> 음식주문 로그에 기록
                    mainFrame.refreshUserInfo();
                    loadFoodList(); // 재고 갱신
                } else {
                    JOptionPane.showMessageDialog(this, "실패: " + result);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "올바른 숫자를 입력하세요.");
            }
        });

        loadFoodList();
    }                                                                                                                                       
    private void loadFoodList() {                                                                                                      
        tableModel.setRowCount(0);                                                                                           
        List<FoodDTO> list = mainFrame.dao.getAllFoods();                                                                         
        for (FoodDTO f : list) {                                                                                                       
            tableModel.addRow(new Object[]{f.getFood_idx(), f.getFood_name(), f.getFood_price(), f.getFood_stock()});                                                           
        }                                                                                                                              
    }                                                                                                                                  
}