package frame.frameUser;

import javax.swing.*;

import java.awt.*;


// 다른 패키지의 클래스 import
import db.PcCafeDAO;
import vo.MemberDTO;


public class PcCafeGUI extends JDialog {
    
    public PcCafeDAO dao = new PcCafeDAO();
    public JLabel lblStatus;
    private MemberDTO currentLoginMember; // Renamed and made private

    public PcCafeGUI(MemberDTO loginMember) {
        this.currentLoginMember = loginMember; // Assign constructor parameter
        setTitle("PC방 회원 메뉴 (MVC)");
        setSize(800, 600);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 상단 내 정보 패널
        JPanel infoPanel = new JPanel();
        lblStatus = new JLabel("로딩 중...");
        lblStatus.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        infoPanel.add(lblStatus);
        infoPanel.setBackground(Color.LIGHT_GRAY);
        add(infoPanel, BorderLayout.NORTH);

        // 2. 탭 패널
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("시간 충전", new TimePanel(this, loginMember)); // Main 프레임을 넘겨줌
        tabPane.addTab("음식 주문", new FoodPanel(this, currentLoginMember));
        tabPane.addTab("잔액 충전", new MoneyPanel(this, loginMember));
        add(tabPane, BorderLayout.CENTER);

        refreshUserInfo();
        setVisible(true);
    }

    // 상단 정보 갱신 메소드
    public void refreshUserInfo() {
        MemberDTO mem = dao.getMember(currentLoginMember.getMem_idx());
        if (mem != null) {
            lblStatus.setText(String.format(" [회원: %s]  잔여 시간: %d분  |  보유 금액: %d원 ", 
                              mem.getMem_name(), mem.getMem_time(), mem.getMem_money()));
        }
    }

}

