package frame.frameAdmin;

import db.MemberDAO;
import db.SeatDAO;
import vo.SeatDTO;

import javax.swing.*;
import java.awt.*;
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
        setBackground(Color.LIGHT_GRAY); 

        // ===== 상단: memIdx 입력 + 새로고침 =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("회원번호:"));

        tfMemIdx = new JTextField(10);
        top.add(tfMemIdx);

        JButton btnRefresh = new JButton("새로고침");
        btnRefresh.addActionListener(e -> refreshSeats());
        top.add(btnRefresh);

        lblInfo = new JLabel("빈좌석 클릭: 착석/이동 자동 처리. 사용중 좌석 클릭: 퇴실/강퇴 처리."); 
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
        btn.setText(seatIdx + "번"); 

      
        if (s.getStatus() == 1) { 
            int age = s.getMemAge();
            String name = s.getMemName();
            if (name == null) {
                // If memName is null in SeatDTO, try to fetch it from MemberDAO
                int memIdx = s.getMemIdx(); // SeatDTO has getMemIdx()
                name = mDao.getMemberName(memIdx); // MemberDAO has getMemberName(int memIdx)
                if (name == null) {
                    name = "이름 없음"; // Fallback if name is still null
                }
            }
            if(age >= 19) {
                btn.setBackground(Color.PINK);
                btn.setForeground(Color.BLACK);
                btn.setText("<html>"+ seatIdx + "번<br>" + name + "</html>");
                
            }
          
        } else { 
            btn.setBackground(Color.GREEN);
            btn.setForeground(Color.BLACK);
            btn.setText(seatIdx + " 빈좌석");
        }
        btn.addActionListener(e -> onSeatButtonClicked(s, seatIdx));
        return btn;
    }

    private void onSeatButtonClicked(SeatDTO s, int clickedSeatIdx) {
        Integer memIdxInput = parseMemIdx();

        // memIdx를 입력 안했을때.
        if (memIdxInput == null) {
            JOptionPane.showMessageDialog(this, "좌석 조작을 위해 회원번호를 입력하세요.");
            return;
        }

        // memIdx를 입력받았지만 존재하지 않을때.
        if (!mDao.isMemberExist(memIdxInput)) {
            JOptionPane.showMessageDialog(this, "존재하지 않는 회원 번호(memIdx)입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 입력받은 memIdx가 좌석에 존재하는지 확인
        int fromSeat = sDao.findSeat(memIdxInput);

        //사용중인 좌석을 클릭했을때
        if (s.getStatus() == 1) { 
            // 사용중인 좌석을 클릭했는데 입력받은 memIdx일때.(강퇴로직)
            if (fromSeat == clickedSeatIdx) {
                int ok = JOptionPane.showConfirmDialog(
                        this,
                        memIdxInput + " 회원을 " + clickedSeatIdx + "번 좌석에서 퇴실(강퇴)시킬까요?",
                        "퇴실/강퇴",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok == JOptionPane.YES_OPTION) {
                    boolean success = sDao.leaveSeat(memIdxInput);
                    if (!success) JOptionPane.showMessageDialog(this, "퇴실 실패(상태가 바뀌었을 수 있어요).");
                    refreshSeats();
                }
            } else { 
                JOptionPane.showMessageDialog(this, "선택한 회원(" + memIdxInput + ")은 " + clickedSeatIdx + "번 좌석에 없습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
            }
        } else { //빈좌석 클릭
            if (fromSeat == 0) { 
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
