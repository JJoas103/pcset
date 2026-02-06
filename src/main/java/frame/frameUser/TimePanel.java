package frame.frameUser;

import javax.swing.*;

import vo.MemberDTO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import db.LogDAO;



public class TimePanel extends JPanel {

    PcCafeGUI mainFrame; // Make it non-final

    MemberDTO loginMember; // Instance variable

    private LogDAO logDAO; // LogDAO 인스턴스 추가

    

    public TimePanel(PcCafeGUI mainFrame, MemberDTO loginMember) {

        this.mainFrame = mainFrame;

        this.loginMember = loginMember; // Assignment

        this.logDAO = new LogDAO(); // LogDAO 초기화

        setLayout(new GridLayout(5, 1, 10, 10)); // 5 rows, 1 column, with gaps

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding



        // Define time options and their costs

        int[][] timeOptions = {

            {60, 1000, 1}, // 1 hour, 1000 won

            {120, 2000, 2}, // 2 hours, 2000 won

            {180, 3000, 3}, // 3 hours, 3000 won

            {300, 5000, 5}, // 5 hours, 5000 won

            {600, 10000, 10} // 10 hours, 10000 won

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

            int memberIdx = loginMember.getMem_idx();

            if (mainFrame.dao.chargeTime(memberIdx, addTime, cost)) {

                JOptionPane.showMessageDialog(mainFrame, 

                                            String.format("%d시간이 충전되었습니다. 금액: %d원", addTime / 60, cost), 

                                            "시간 충전", 

                                            JOptionPane.INFORMATION_MESSAGE);

                logDAO.insertLog(loginMember.getMem_idx(), 1, cost); // 시간 충전 로그 기록 (log_type: 1)

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
