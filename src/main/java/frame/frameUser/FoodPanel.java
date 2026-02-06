package frame.frameUser;                                                                                                                            
                                                                                                                                       
import javax.swing.*;                                                                                                                  
import javax.swing.table.DefaultTableModel;

import db.PcCafeDAO;
import db.SeatDAO; // SeatDAO 임포트 추가
import db.LogDAO; // LogDAO 임포트 추가

import java.awt.*;
import java.util.List;

import vo.FoodDTO;
import vo.MemberDTO;
import vo.OrdersDTO;

public class FoodPanel extends JPanel {
    PcCafeGUI mainFrame; // Make it non-final
    MemberDTO loginMember; // Store loginMember
    DefaultTableModel tableModel;
    JTable foodTable;
    private SeatDAO seatDAO = new SeatDAO(); // SeatDAO 인스턴스 생성
    private LogDAO logDAO = new LogDAO(); // LogDAO 인스턴스 생성

    public FoodPanel(PcCafeGUI mainFrame, MemberDTO loginMember) {
        this.mainFrame = mainFrame; // Assign mainFrame
        this.loginMember = loginMember; // Assign loginMember
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
                    logDAO.insertLog(loginMember.getMem_idx(), 2, foodPrice * qty); // 음식 주문 로그 기록 (log_type: 2)
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