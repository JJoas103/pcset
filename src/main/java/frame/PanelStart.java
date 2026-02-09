package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelStart extends JPanel {

    public PanelStart() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 800));
        setBackground(Color.WHITE);

        //============== 상단 이미지 패널 ==============================
        ImageIcon originalIcon = new ImageIcon("src\\main\\java\\img\\mainImage.png");
        Image scaledImg = originalIcon.getImage().getScaledInstance(600, 550, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(imgLabel, BorderLayout.CENTER);
        imgPanel.setBackground(Color.WHITE);

        add(imgPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        add(centerPanel, BorderLayout.CENTER);

        //================= 하단 버튼 패널 ====================
        JPanel btnPanel = new JPanel(null);
        btnPanel.setPreferredSize(new Dimension(600, 200));
        btnPanel.setBackground(Color.WHITE);

        int panelWidth = 600;
        int buttonWidth = 150;
        int buttonHeight = 60;
        int gap = 30;
        int totalButtonWidth = (buttonWidth * 3) + (gap * 2);
        int btnStartX = (panelWidth - totalButtonWidth) / 2;
        int buttonY = (200 - buttonHeight) / 2;

        // 로그인 버튼
        JButton btnLogin = new JButton("로그인");
        btnLogin.setBounds(btnStartX, buttonY, buttonWidth, buttonHeight);
        btnLogin.setFont(new Font("나눔고딕", Font.BOLD, 22));
        
        // 회원가입 버튼
        JButton btnJoin = new JButton("회원가입");
        btnJoin.setBounds(btnStartX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight);
        btnJoin.setFont(new Font("나눔고딕", Font.BOLD, 22));
        
        // 종료 버튼
        JButton btnExit = new JButton("종료");
        btnExit.setBounds(btnStartX + (buttonWidth + gap) * 2, buttonY, buttonWidth, buttonHeight);
        btnExit.setFont(new Font("나눔고딕", Font.BOLD, 22));

        btnPanel.add(btnLogin);
        btnPanel.add(btnJoin);
        btnPanel.add(btnExit);

        add(btnPanel, BorderLayout.SOUTH);

        //======================= 버튼 이벤트 ========================
        // 로그인 버튼 클릭 시 로그인 패널로 전환
        btnLogin.addActionListener(e -> FrameBase.getInstance(new PanelLogin()));

        // 회원가입 버튼 클릭 시 회원가입 패널로 전환
        btnJoin.addActionListener(e -> FrameBase.getInstance(new PanelJoin()));

        // 종료 버튼 클릭 시 프로그램 종료
        btnExit.addActionListener(e -> System.exit(0));

        
    }
}