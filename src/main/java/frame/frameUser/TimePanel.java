package frame.frameUser;

import javax.swing.*;

import vo.MemberDTO;
import vo.TimeDTO;
import java.util.List;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import db.LogDAO;
import db.TimeDAO;


public class TimePanel extends JPanel {

    PanelChargeWrapper parentPanel; 
    private LogDAO logDAO;
    private TimeDAO timeDAO;

    public TimePanel(PanelChargeWrapper parentPanel) { // Modified constructor

        this.parentPanel = parentPanel;
        this.logDAO = new LogDAO();
        this.timeDAO = new TimeDAO();
        setLayout(new GridLayout(0, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        // [재활용 지점] 작성하신 DAO 메서드 호출
        List<TimeDTO> timeMenuList = timeDAO.getAllTime();

        for (TimeDTO time : timeMenuList) {
            int hour = time.getHour();
            int cost = time.getPrice();
            int addTime = hour * 60; // DB에는 시간이 분으로 저장됨

            JButton btn = new JButton(String.format("%d시간 (%d원)", hour, cost));
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 20));
            
            // 기존의 액션 리스너 연결
            btn.addActionListener(new TimeChargeActionListener(addTime, cost));
            add(btn);
        }
    }

    private class TimeChargeActionListener implements ActionListener {
       
        private int addTime;
        private int cost;

        public TimeChargeActionListener(int addTime, int cost) {
            this.addTime = addTime;
            this.cost = cost;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Fetch fresh MemberDTO before charging to ensure correct mem_idx
            MemberDTO freshMember = parentPanel.dao.getMember(parentPanel.getCurrentLoginMember().getMem_idx()); // Use parentPanel's current member for mem_idx
            if (freshMember == null) {
                JOptionPane.showMessageDialog(parentPanel, "회원 정보를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirmResult = JOptionPane.showConfirmDialog(parentPanel,
                                                            String.format("%d시간을 충전 하시겠습니까?", addTime / 60),
                                                            "시간 충전 확인",
                                                            JOptionPane.YES_NO_OPTION);
            int memberIdx = freshMember.getMem_idx(); // Use mem_idx from fresh MemberDTO

            if(confirmResult == JOptionPane.YES_OPTION) {
                if (parentPanel.dao.chargeTime(memberIdx, addTime, cost)) {
                JOptionPane.showMessageDialog(parentPanel, 
                                            String.format("%d시간이 충전되었습니다. 금액: %d원", addTime / 60, cost), 
                                            "시간 충전", 
                                            JOptionPane.INFORMATION_MESSAGE);
                logDAO.insertLog(memberIdx, 1, cost); // Use fresh memberIdx for log
                parentPanel.refreshUserInfo();
            } else {
                JOptionPane.showMessageDialog(parentPanel, 
                                            "금액이 부족합니다.", 
                                            "충전 실패", 
                                            JOptionPane.WARNING_MESSAGE);
            }
            }
            
        }
    }
}
