package frame;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.*;

import db.MemberDAO;
import frame.frameUser.PanelUser;
import vo.MemberDTO;

public class PanelFoodOrder extends JPanel{
    public PanelFoodOrder(MemberDTO loginMember) {

        MemberDAO memberDAO = new MemberDAO();
        // 패널 기본 설정
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 800));
        setBackground(Color.WHITE);

        //============== 상단 이미지 패널 ==============================
        ImageIcon originalIcon = new ImageIcon("src\\main\\java\\img\\loginImage.png");
        Image scaledImg = originalIcon.getImage().getScaledInstance(600, 480, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(imgLabel, BorderLayout.CENTER);
        imgPanel.setBackground(Color.WHITE);

        add(imgPanel, BorderLayout.NORTH);
        
        //============== 중단 입력 패널 ===============================
        JPanel inputPanel = new JPanel(null);
        inputPanel.setPreferredSize(new Dimension(600, 150));
        inputPanel.setBackground(Color.WHITE);

        //충전 라벨과 충전 입력 필드
        JLabel CLabel = new JLabel("충전할 금액");
        CLabel.setBounds(120, 10, 80, 30);
        CLabel.setFont(new Font("나눔고딕코딩", Font.BOLD, 18));
        JTextField CField = new JTextField();
        CField.setBounds(230, 10, 250, 30);
        CField.setFont(new Font("나눔고딕코딩", Font.BOLD, 18));

        inputPanel.add(CLabel);
        inputPanel.add(CField);

        add(inputPanel, BorderLayout.CENTER);

        // ====================== 하단 버튼 패널 =======================
        JPanel btnPanel = new JPanel(null);
        btnPanel.setPreferredSize(new Dimension(600, 100));
        btnPanel.setBackground(Color.WHITE);

        // 충전하기 버튼
        JButton btnCharge = new JButton("금액 충전");
        btnCharge.setBounds(50, 5, 140, 90);
        btnCharge.setFont(new Font("나눔고딕코딩", Font.BOLD, 20));
        btnPanel.add(btnCharge);

        // 뒤로가기 버튼
        JButton btnBack = new JButton("뒤로가기");
        btnBack.setBounds(410, 5, 140, 90);
        btnBack.setFont(new Font("나눔고딕코딩", Font.BOLD, 20));
        
        btnPanel.add(btnBack);

        add(btnPanel, BorderLayout.SOUTH);

        // 충전하기 버튼 이벤트
        btnCharge.addActionListener(e -> {
            int inputPay = Integer.parseInt(CField.getText());
            String checkEmpty = CField.getText();
            if(inputPay == 0) {
                JOptionPane.showMessageDialog(this, "충전할 금액이 없습니다!!", "입력 오류", JOptionPane.WARNING_MESSAGE);
            }
            if(checkEmpty.isEmpty()){
                 JOptionPane.showMessageDialog(this, "항목을 입력하세요!!", "입력 오류", JOptionPane.WARNING_MESSAGE);

            }
            int charge = loginMember.getMem_money() + inputPay;
            JOptionPane.showMessageDialog(this, "충전이 완료되었습니다. 현재 잔액은 " + charge + "원 입니다.", "충전 완료", JOptionPane.INFORMATION_MESSAGE);

            memberDAO.chargeMoney(loginMember, inputPay);
            FrameBase.getInstance(new PanelUser(loginMember));

        });

        // 뒤로가기 버튼 이벤트
        btnBack.addActionListener(e -> {
            FrameBase.getInstance(new PanelUser(loginMember));
        });

        

    }
}
