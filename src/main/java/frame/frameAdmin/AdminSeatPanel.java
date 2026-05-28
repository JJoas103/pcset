package frame.frameAdmin;

import db.MemberDAO;
import db.SeatDAO;
import vo.SeatDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminSeatPanel extends JPanel {

    private final SeatDAO sDao;
    private final MemberDAO mDao;

    private JPanel seatPanelGrid;
    private JTextField tfMemIdx;
    private JLabel lblInfo;

    public AdminSeatPanel(SeatDAO sDao, MemberDAO mDao) {
        this.sDao = sDao;
        this.mDao = mDao;

        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);

        // ===== 상단 =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("회원번호:"));

        tfMemIdx = new JTextField(10);
        top.add(tfMemIdx);

        JButton btnRefresh = new JButton("새로고침");
        btnRefresh.addActionListener(e -> refreshSeats());
        top.add(btnRefresh);

        lblInfo = new JLabel("숫자 입력: 착석/이동/강퇴 | '수리중' 입력: 좌석 클릭 → 수리중(2)/해제(0)");
        top.add(lblInfo);

        // ===== 가운데: 좌석 그리드 =====
        seatPanelGrid = new JPanel(new GridLayout(5, 10, 8, 8));
        seatPanelGrid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        seatPanelGrid.setBackground(Color.LIGHT_GRAY);

        add(top, BorderLayout.NORTH);
        add(seatPanelGrid, BorderLayout.CENTER);

        refreshSeats();
    }

    private void refreshSeats() {
        seatPanelGrid.removeAll();

        List<SeatDTO> seats = sDao.getAllSeats();
        for (SeatDTO s : seats) {
            seatPanelGrid.add(makeSeatButton(s));
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

        // status=2 수리중
        if (s.getStatus() == 2) {
            btn.setBackground(Color.DARK_GRAY);
            btn.setForeground(Color.WHITE);
            btn.setText(seatIdx + " 수리중");
        }
        // status=1 사용중
        else if (s.getStatus() == 1) {
            String name = s.getMemName();
            if (name == null || name.trim().isEmpty()) {
                name = "회원";
            }

            int age = s.getMemAge(); // SeatDTO가 int면 null 불가. 0일 수도 있음.

            if (age >= 19) {
                btn.setBackground(Color.PINK);
                btn.setForeground(Color.BLACK);
                btn.setText("<html>" + seatIdx + "번<br>" + name + "(" + s.getMemIdx() + ")</html>");
            } else {
                btn.setBackground(Color.YELLOW);
                btn.setForeground(Color.BLACK);
                btn.setText("<html>" + seatIdx + "번<br>" + name + "(" + s.getMemIdx() + ")</html>");
                btn.setToolTipText("미성년자(" + age + "세) - 22시 이후 퇴실 대상");
            }
        }
        // status=0 빈좌석
        else {
            btn.setBackground(Color.GREEN);
            btn.setForeground(Color.BLACK);
            btn.setText(seatIdx + " 빈좌석");
        }

        btn.addActionListener(e -> onSeatButtonClicked(s, seatIdx));
        return btn;
    }

    private void onSeatButtonClicked(SeatDTO clickedSeat, int clickedSeatIdx) {

        // 1) 수리중 모드: 토글
        if (isRepairCommand()) {
            int nextStatus = (clickedSeat.getStatus() == 2) ? 0 : 2;

            String msg = (nextStatus == 2)
                    ? clickedSeatIdx + "번 좌석을 '수리중'으로 설정할까요?"
                    : clickedSeatIdx + "번 좌석의 '수리중'을 해제하고 빈좌석으로 바꿀까요?";

            int ok = JOptionPane.showConfirmDialog(this, msg, "수리중 토글", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                boolean success = sDao.setRepairStatus(clickedSeatIdx, nextStatus);
                if (!success) JOptionPane.showMessageDialog(this, "변경 실패(상태가 바뀌었을 수 있어요).");
                refreshSeats();
            }
            return;
        }

        // 2) 일반 모드: 회원번호 숫자
        Integer memIdxInput = parseMemIdx();
        if (memIdxInput == null) {
            JOptionPane.showMessageDialog(this, "회원번호(memIdx)를 입력하거나 '수리중'을 입력하세요.");
            return;
        }

        if (!mDao.isMemberExist(memIdxInput)) {
            JOptionPane.showMessageDialog(this, "존재하지 않는 회원 번호(memIdx)입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 입력한 회원의 이름 (빈좌석에서 s.getMemName() 쓰면 null이므로 여기서 가져와야 함)
        String inputMemberName = mDao.getMemberName(memIdxInput);
        if (inputMemberName == null || inputMemberName.trim().isEmpty()) {
            inputMemberName = "회원(" + memIdxInput + ")";
        }

        // 해당 회원이 이미 앉아있는 좌석
        int fromSeat = sDao.findSeat(memIdxInput);

        // 수리중 좌석 클릭은 불가
        if (clickedSeat.getStatus() == 2) {
            JOptionPane.showMessageDialog(this, "수리중 좌석입니다. 다른 좌석을 선택하세요.", "수리중", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3) 사용중 좌석 클릭 -> 강퇴(해당 회원 자리일 때만)
        if (clickedSeat.getStatus() == 1) {

            String seatedName = clickedSeat.getMemName();
            if (seatedName == null || seatedName.trim().isEmpty()) {
                seatedName = "회원(" + clickedSeat.getMemIdx() + ")";
            }

            if (fromSeat == clickedSeatIdx) {
                int ok = JOptionPane.showConfirmDialog(
                        this,
                        seatedName + " 회원을 " + clickedSeatIdx + "번 좌석에서 퇴실(강퇴)시킬까요?",
                        "퇴실/강퇴",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok == JOptionPane.YES_OPTION) {
                    boolean success = sDao.leaveSeat(memIdxInput);
                    if (!success) JOptionPane.showMessageDialog(this, "퇴실 실패(상태가 바뀌었을 수 있어요).");
                    refreshSeats();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "선택한 회원(" + memIdxInput + ")은 " + clickedSeatIdx + "번 좌석에 없습니다.",
                        "정보", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }

        // 4) 빈좌석 클릭 -> 착석 or 이동
        if (fromSeat == 0) {
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    inputMemberName + " 님을(를) " + clickedSeatIdx + "번 좌석에 착석시킬까요?",
                    "착석",
                    JOptionPane.YES_NO_OPTION
            );
            if (ok == JOptionPane.YES_OPTION) {
                boolean success = sDao.selectSeat(clickedSeatIdx, memIdxInput);
                if (!success) JOptionPane.showMessageDialog(this, "착석 실패(이미 누가 앉았을 수 있어요).");
                refreshSeats();
            }
        } else {
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

    private boolean isRepairCommand() {
        String text = tfMemIdx.getText();
        if (text == null) return false;
        text = text.trim();
        return text.equalsIgnoreCase("수리중") || text.equalsIgnoreCase("repair");
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