package frame.frameAdmin;

import db.SeatDAO;
import db.MemberDAO; // MemberDAO 임포트 추가
import vo.MemberDTO;
import vo.SeatDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminSeat extends JDialog {

    private SeatDAO seatDAO = new SeatDAO();
    private MemberDAO memberDAO = new MemberDAO(); // MemberDAO 인스턴스 생성
    private JPanel seatPanel;
    // private JTextField tfMemIdx; // 더 이상 사용하지 않으므로 주석 처리
    private JLabel lblInfo;
    private MemberDTO currentLoginMember; // loginMember를 인스턴스 변수로 선언

    public AdminSeat(MemberDTO loginMember) {
        this.currentLoginMember = loginMember; // 인스턴스 변수 초기화

        setTitle("PC방 관리자 - 좌석(전체/선택/이동) 데모");
        setSize(1200, 800);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(buildRoot()); // 매개변수 없이 호출
        setVisible(true);

        refreshSeats(); // 매개변수 없이 호출
    }

    private JPanel buildRoot() { // 매개변수 제거
        JPanel root = new JPanel(new BorderLayout());

        // ===== 상단: memIdx 입력 + 새로고침 =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // top.add(new JLabel("memIdx:")); // 더 이상 수동 입력 필드가 아님
        // tfMemIdx = new JTextField(10);
        // top.add(tfMemIdx);

        JButton btnRefresh = new JButton("새로고침");
        btnRefresh.addActionListener(e -> refreshSeats()); // 매개변수 없이 호출
        top.add(btnRefresh);

        lblInfo = new JLabel("현재 회원: " + currentLoginMember.getMem_name() + " (" + currentLoginMember.getMem_idx() + ")"); // 정보 표시 변경
        top.add(lblInfo);

        // ===== 가운데: 좌석 그리드 =====
        seatPanel = new JPanel(new GridLayout(5, 10, 8, 8));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        root.add(top, BorderLayout.NORTH);
        root.add(seatPanel, BorderLayout.CENTER);
        return root;
    }

    private void refreshSeats() { // 매개변수 제거
        seatPanel.removeAll();

        List<SeatDTO> seats = seatDAO.getAllSeats();
        for (SeatDTO s : seats) {
            JButton btn = makeSeatButton(s); // 매개변수 없이 호출
            seatPanel.add(btn);
        }

        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private JButton makeSeatButton(SeatDTO s) { // 매개변수 제거
        int seatIdx = s.getSeatIdx();

        JButton btn = new JButton();
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);

        if (s.getStatus() == 1) {
            btn.setBackground(Color.RED);
            btn.setForeground(Color.WHITE);
            btn.setText(seatIdx + " (사용중)");
            btn.setEnabled(false);
        } else {
            btn.setBackground(Color.GREEN);
            btn.setForeground(Color.BLACK);
            btn.setText(seatIdx + " (빈좌석)");
            btn.setEnabled(true);

            btn.addActionListener(e -> onEmptySeatClicked(seatIdx)); // 매개변수 없이 호출
        }

        return btn;
    }

    private void onEmptySeatClicked(int targetSeatIdx) { // 매개변수 제거
        Integer memIdx = currentLoginMember.getMem_idx(); // 인스턴스 변수 직접 사용
        
        // 회원 존재 여부 확인 (currentLoginMember의 mem_idx는 유효하다고 가정)
        // 이 검사는 일반적으로는 loginMember가 유효한 경우 필요 없을 수 있으나, 혹시 모를 상황 대비
        if (!memberDAO.isMemberExist(memIdx)) { 
            JOptionPane.showMessageDialog(this, "오류: 현재 로그인한 회원의 정보가 유효하지 않습니다.");
            return;
        }

        int fromSeat = seatDAO.findSeat(memIdx);

        // 1) 현재 좌석 없음 -> 착석
        if (fromSeat == 0) {
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    memIdx + " 회원을 " + targetSeatIdx + "번 좌석에 착석시킬까요?",
                    "착석",
                    JOptionPane.YES_NO_OPTION
            );

            if (ok == JOptionPane.YES_OPTION) {
                boolean success = seatDAO.selectSeat(targetSeatIdx, memIdx);
                if (!success) JOptionPane.showMessageDialog(this, "착석 실패(이미 누가 앉았을 수 있어요).");
                refreshSeats(); // 매개변수 없이 호출
            }
            return;
        }

        // 2) 이미 좌석 있음 -> 이동
        if (fromSeat == targetSeatIdx) {
            JOptionPane.showMessageDialog(this, "이미 그 좌석에 앉아있습니다.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                fromSeat + "번 → " + targetSeatIdx + "번으로 이동할까요?",
                "이동",
                JOptionPane.YES_NO_OPTION
        );

        if (ok == JOptionPane.YES_OPTION) {
            boolean success = seatDAO.moveSeat(memIdx, fromSeat, targetSeatIdx);
            if (!success) JOptionPane.showMessageDialog(this, "이동 실패(좌석 상태가 바뀌었을 수 있어요).");
            refreshSeats(); // 매개변수 없이 호출
        }
    }
}