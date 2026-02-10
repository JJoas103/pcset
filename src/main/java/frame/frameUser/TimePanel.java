package frame.frameUser;

import javax.swing.*;

import vo.MemberDTO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import db.LogDAO;


public class TimePanel extends JPanel {

    UserView mainFrame; 
    MemberDTO loginMember;
    private LogDAO logDAO;

    public TimePanel(UserView mainFrame, MemberDTO loginMember) {

        this.mainFrame = mainFrame;
        this.loginMember = loginMember; 
        this.logDAO = new LogDAO();

        setLayout(new GridLayout(5, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        int[][] timeOptions = {

            {60, 1000, 1},
            {120, 2000, 2},
            {180, 3000, 3}, 
            {300, 5000, 5}, 
            {600, 10000, 10} 
        };

        for (int[] option : timeOptions) {
            int addTime = option[0];
            int cost = option[1];
            int displayTime = option[2];
            JButton btn = new JButton(String.format("%d시간 (%d원)", displayTime, cost));
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 20));
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
            MemberDTO freshMember = mainFrame.dao.getMember(loginMember.getMem_idx());
            if (freshMember == null) {
                JOptionPane.showMessageDialog(mainFrame, "회원 정보를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int memberIdx = freshMember.getMem_idx(); // Use mem_idx from fresh MemberDTO

            if (mainFrame.dao.chargeTime(memberIdx, addTime, cost)) {
                JOptionPane.showMessageDialog(mainFrame, 
                                            String.format("%d시간이 충전되었습니다. 금액: %d원", addTime / 60, cost), 
                                            "시간 충전", 
                                            JOptionPane.INFORMATION_MESSAGE);
                logDAO.insertLog(memberIdx, 1, cost); // Use fresh memberIdx for log
                mainFrame.refreshUserInfo();
            } else {
                JOptionPane.showMessageDialog(mainFrame, 
                                            "금액이 부족합니다.", 
                                            "충전 실패", 
                                            JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
