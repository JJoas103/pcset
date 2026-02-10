package frame.frameUser;

import db.MemberDAO;
import db.PcCafeDAO;
import vo.MemberDTO;
import frame.FrameBase;
import frame.PanelStart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PanelChargeWrapper extends JPanel {

    public PcCafeDAO dao = new PcCafeDAO(); // This dao is for PcCafeDAO methods like getMember, chargeTime
    private MemberDAO memberDAO; // This memberDAO is for MemberDAO methods like getAllMembers, chargeMoney

    public JLabel lblStatus;
    private MemberDTO currentLoginMember;
    private JPanel infoPanel;

    public PanelChargeWrapper(MemberDTO loginMember) {
        this.currentLoginMember = loginMember;
        this.memberDAO = new MemberDAO(); 
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600)); 
        setBackground(Color.WHITE);

        // 1. 상단 내 정보 패널
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.LIGHT_GRAY);

        lblStatus = new JLabel("로딩 중...");
        lblStatus.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER); 
        infoPanel.add(lblStatus, BorderLayout.CENTER);

        // "돌아가기" 버튼 추가
        JButton btnBack = new JButton("돌아가기");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Return logic: check if time is charged, then go to PanelUser or PanelStart
                MemberDTO updatedMember = null;
                // Fetch fresh member info for navigation decision
                updatedMember = dao.getMember(currentLoginMember.getMem_idx());


                if (updatedMember != null && updatedMember.getMem_time() > 0) {
                    FrameBase.getInstance(new PanelUser(updatedMember));
                } else {
                    JOptionPane.showMessageDialog(PanelChargeWrapper.this, "시간이 부족하거나 충전되지 않아 서비스 이용이 제한됩니다.", "안내", JOptionPane.WARNING_MESSAGE);
                    FrameBase.getInstance(new PanelStart());
                }
            }
        });
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.setOpaque(false); 
        backButtonPanel.add(btnBack);
        infoPanel.add(backButtonPanel, BorderLayout.EAST);
        
        add(infoPanel, BorderLayout.NORTH);

        // 2. 탭 패널
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("시간 충전", new TimePanel(this)); 
        tabPane.addTab("음식 주문", new FoodPanel(this));
        tabPane.addTab("잔액 충전", new MoneyPanel(this));
        add(tabPane, BorderLayout.CENTER);

        refreshUserInfo();
    }

    // 상단 정보 갱신 메소드
    public void refreshUserInfo() {
        MemberDTO mem = dao.getMember(currentLoginMember.getMem_idx());
        if (mem != null) {
            currentLoginMember = mem; 
            lblStatus.setText(String.format(" [회원: %s]  잔여 시간: %d분  |  보유 금액: %d원 ", 
                              mem.getMem_name(), mem.getMem_time(), mem.getMem_money()));
            infoPanel.revalidate(); 
            infoPanel.repaint();    
        } else {
            // Handle case where member info cannot be retrieved (e.g., deleted member)
            JOptionPane.showMessageDialog(this, "회원 정보를 가져올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            FrameBase.getInstance(new PanelStart()); // Go back to start if member not found
        }
    }

    // Getter for currentLoginMember to be used by child panels
    public MemberDTO getCurrentLoginMember() {
        return currentLoginMember;
    }
}