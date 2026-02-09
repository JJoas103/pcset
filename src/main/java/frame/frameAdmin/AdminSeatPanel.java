package frame.frameAdmin;

import db.MemberDAO;
import db.SeatDAO;
import vo.SeatDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener; // Added for clarity
import java.util.ArrayList;
import java.util.List;

public class AdminSeatPanel extends JPanel {

    private SeatDAO sDao;
    private MemberDAO mDao; 
    private JPanel seatPanelGrid;
    private JTextField tfMemIdx;
    private JLabel lblInfo;
    
    public AdminSeatPanel(SeatDAO sDao, MemberDAO mDao) {
        this.sDao = sDao;
        this.mDao = mDao;
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY); // Mimic UserView background

        // ===== 상단: memIdx 입력 + 새로고침 =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("memIdx:"));

        tfMemIdx = new JTextField(10);
        top.add(tfMemIdx);

        JButton btnRefresh = new JButton("새로고침");
        btnRefresh.addActionListener(e -> refreshSeats());
        top.add(btnRefresh);

        lblInfo = new JLabel("빈좌석 클릭: 착석/이동 자동 처리. 사용중 좌석 클릭: 퇴실/강퇴 처리."); // Updated info text
        top.add(lblInfo);
        
        // ===== 가운데: 좌석 그리드 =====
        seatPanelGrid = new JPanel(new GridLayout(5, 10, 8, 8));
        seatPanelGrid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        seatPanelGrid.setBackground(Color.LIGHT_GRAY); // Mimic UserView background

        add(top, BorderLayout.NORTH);
        add(seatPanelGrid, BorderLayout.CENTER);
        
        refreshSeats(); // Initial data load
    }

    private void refreshSeats() { // Renamed from refreshSeatData to refreshSeats to match UserView
        seatPanelGrid.removeAll();

        List<SeatDTO> seats = sDao.getAllSeats();
        for (SeatDTO s : seats) {
            JButton btn = makeSeatButton(s);
            seatPanelGrid.add(btn);
        }

        seatPanelGrid.revalidate();
        seatPanelGrid.repaint();
    }

    private JButton makeSeatButton(SeatDTO s) {
        int seatIdx = s.getSeatIdx();
        JButton btn = new JButton();
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setText(seatIdx + "번"); // Default text

        // UI for occupied/empty seats (design mimic UserView)
        if (s.getStatus() == 1) { // Occupied
            btn.setBackground(Color.RED);
            btn.setForeground(Color.WHITE);
            // Optionally show member name on button for admin
            btn.setText("<html>"+ seatIdx + "번<br>" + s.getMemName() + "</html>");
        } else { // Empty
            btn.setBackground(Color.GREEN);
            btn.setForeground(Color.BLACK);
            btn.setText(seatIdx + " 빈좌석");
        }

        // Attach a single listener for all seat buttons
        btn.addActionListener(e -> onSeatButtonClicked(s, seatIdx));

        return btn;
    }

    private void onSeatButtonClicked(SeatDTO s, int clickedSeatIdx) {
        Integer memIdxInput = parseMemIdx();

        // Admin functionality (evict/move/sit)
        // Scenario 1: memIdx is NOT provided in the input field
        if (memIdxInput == null) {
            // If admin clicks an occupied seat without memIdx, maybe just show info or do nothing
            // If admin clicks an empty seat without memIdx, do nothing
            JOptionPane.showMessageDialog(this, "좌석 조작을 위해 memIdx를 입력하세요.");
            return;
        }

        // Scenario 2: memIdx IS provided in the input field
        // Validate member existence
        if (!mDao.isMemberExist(memIdxInput)) {
            JOptionPane.showMessageDialog(this, "존재하지 않는 회원 번호(memIdx)입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Find if the memIdxInput is already seated
        int fromSeat = sDao.findSeat(memIdxInput);

        // Action depends on clicked seat status and member's current seat
        if (s.getStatus() == 1) { // Clicked an occupied seat (admin wants to evict/move this member)
            if (fromSeat == clickedSeatIdx) { // If the member in memIdxInput is on THIS seat
                // Option to evict/checkout this member from this seat
                int ok = JOptionPane.showConfirmDialog(
                        this,
                        memIdxInput + " 회원을 " + clickedSeatIdx + "번 좌석에서 퇴실(강퇴)시킬까요?",
                        "퇴실/강퇴",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok == JOptionPane.YES_OPTION) {
                    boolean success = sDao.leaveSeat(memIdxInput); // leaveSeat uses memIdx
                    if (!success) JOptionPane.showMessageDialog(this, "퇴실 실패(상태가 바뀌었을 수 있어요).");
                    refreshSeats();
                }
            } else { // Clicked an occupied seat, but the memIdxInput is not on it, or on another seat
                JOptionPane.showMessageDialog(this, "선택한 회원(" + memIdxInput + ")은 " + clickedSeatIdx + "번 좌석에 없습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
                // Optionally, could allow moving another user, but that's more complex
            }
        } else { // Clicked an empty seat (admin wants to sit/move memIdxInput to this seat)
            // This logic is mostly what was in onEmptySeatClicked
            if (fromSeat == 0) { // Member not seated, try to sit
                int ok = JOptionPane.showConfirmDialog(
                        this,
                        memIdxInput + " 회원을 " + clickedSeatIdx + "번 좌석에 착석시킬까요?",
                        "착석",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok == JOptionPane.YES_OPTION) {
                    boolean success = sDao.selectSeat(clickedSeatIdx, memIdxInput);
                    if (!success) JOptionPane.showMessageDialog(this, "착석 실패(이미 누가 앉았을 수 있어요).");
                    refreshSeats();
                }
            } else { // Member is seated elsewhere, try to move
                int ok = JOptionPane.showConfirmDialog(
                        this,
                        fromSeat + "번 → " + clickedSeatIdx + "번으로 이동할까요?",
                        "이동",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok == JOptionPane.YES_OPTION) {
                    boolean success = sDao.moveSeat(memIdxInput, fromSeat, clickedSeatIdx);
                    if (!success) JOptionPane.showMessageDialog(this, "이동 실패(좌석 상태가 바뀌었을 수 있어요).");
                    refreshSeats();
                }
            }
        }
    }

    private Integer parseMemIdx() {
        String text = tfMemIdx.getText();
        if (text == null) return null;
        text = text.trim();
        if (text.isEmpty()) return null;

        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return null;
        }
    }
}
