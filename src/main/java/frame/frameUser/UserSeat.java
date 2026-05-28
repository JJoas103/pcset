package frame.frameUser;

import db.SeatDAO;
import vo.MemberDTO;
import vo.SeatDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserSeat extends JDialog {

    private final SeatDAO sDao = new SeatDAO();
    private final MemberDTO loginMember;
    private final int memIdx;

    private JPanel seatPanel;
    private JLabel lblUser;
    private JLabel lblMySeat;

    public UserSeat(MemberDTO loginMember) {
        this.loginMember = loginMember;
        this.memIdx = loginMember.getMem_idx();

        setTitle("PC방 사용자 - 좌석 선택"); 
        setSize(1200, 800); 
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 

        setLayout(new BorderLayout());
        

        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        refreshSeats();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        lblUser = new JLabel("로그인: " + loginMember.getMem_name() + " (memIdx=" + memIdx + ")");
        lblMySeat = new JLabel("내 좌석: 조회중");

        JButton btnRefresh = new JButton("새로고침");
        btnRefresh.addActionListener(e -> refreshSeats());

        top.add(lblUser);
        top.add(new JLabel("|"));
        top.add(lblMySeat);
        top.add(btnRefresh);

        return top;
    }

    private JPanel buildCenter() {
        seatPanel = new JPanel(new GridLayout(5, 10, 8, 8));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        seatPanel.setBackground(Color.LIGHT_GRAY);
        return seatPanel;
    }

    private void refreshSeats() {
        seatPanel.removeAll();

        int mySeat = sDao.findSeat(memIdx);
        lblMySeat.setText(mySeat == 0
                ? "내 좌석: 없음 (빈좌석 클릭 → 착석)"
                : "내 좌석: " + mySeat + "번 (빈좌석 클릭 → 이동, 내자리 클릭 → 퇴실)");

        List<SeatDTO> list = sDao.getAllSeats();

        for (SeatDTO s : list) {
            seatPanel.add(makeSeatButton(s, mySeat));
        }

        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private JButton makeSeatButton(SeatDTO s, int mySeat) {
        int seatIdx = s.getSeatIdx();

        JButton btn = new JButton();
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        // 사용중
        if (s.getStatus() == 1) {
            btn.setForeground(Color.WHITE);
            btn.setEnabled(false);

            // 내가 앉은 자리면 파랑으로 강조 + 클릭 허용(퇴실)
            if (seatIdx == mySeat) {
                btn.setBackground(new Color(52, 152, 219));
                btn.setText(seatIdx + " 내자리");
                btn.setEnabled(true);

                btn.addActionListener(e -> {
                    int ok = JOptionPane.showConfirmDialog(
                            this,
                            "퇴실(자리 비우기) 할까요?",
                            "퇴실",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (ok == JOptionPane.YES_OPTION) {
                        boolean success = sDao.leaveSeat(memIdx);
                        if (!success) JOptionPane.showMessageDialog(this, "퇴실 실패(상태가 바뀌었을 수 있어요).");
                        refreshSeats();
                    }
                });
            } else {
                btn.setBackground(Color.RED);
                btn.setText(seatIdx + " 사용중");
                
            }
        }
        else if(s.getStatus() == 2){
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.GREEN);
            btn.setText(seatIdx + "수리중");
            btn.setEnabled(true);
            
            return btn;
        } 
        else { // 빈좌석
            btn.setBackground(Color.GREEN);
            btn.setForeground(Color.BLACK);
            btn.setText(seatIdx + " 빈좌석");
            btn.setEnabled(true);

            btn.addActionListener(e -> handleEmptySeatClick(seatIdx));
        }

        return btn;
    }

    private void handleEmptySeatClick(int targetSeatIdx) {
        int fromSeat = sDao.findSeat(memIdx);

        // 좌석 없음 → 착석
        if (fromSeat == 0) {
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    targetSeatIdx + "번 좌석에 착석할까요?",
                    "착석",
                    JOptionPane.YES_NO_OPTION
            );
            if (ok == JOptionPane.YES_OPTION) {
                boolean success = sDao.selectSeat(targetSeatIdx, memIdx);
                if (!success) JOptionPane.showMessageDialog(this, "착석 실패(좌석 상태가 바뀌었을 수 있어요).");
                refreshSeats();
            }
            return;
        }

        // 이미 좌석 있음 → 이동
        int ok = JOptionPane.showConfirmDialog(
                this,
                fromSeat + "번 → " + targetSeatIdx + "번으로 이동할까요?",
                "좌석 이동",
                JOptionPane.YES_NO_OPTION
        );
        if (ok == JOptionPane.YES_OPTION) {
            boolean success = sDao.moveSeat(memIdx, fromSeat, targetSeatIdx);
            if (!success) JOptionPane.showMessageDialog(this, "이동 실패(좌석 상태가 바뀌었을 수 있어요).");
            refreshSeats();
        }
    }
}