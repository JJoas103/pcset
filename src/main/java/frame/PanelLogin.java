package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import db.MemberDAO;
import frame.frameAdmin.PanelAdmin;
import frame.frameUser.PanelChargeWrapper; // Changed from UserView
import frame.frameUser.PanelUser;
import vo.MemberDTO;

public class PanelLogin extends JPanel {

    public PanelLogin() {

        MemberDAO memberDAO = new MemberDAO();
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

        //============= 중단 입력 패널 ==============================
        JPanel inputPanel = new JPanel(null);
        inputPanel.setPreferredSize(new Dimension(600, 150));
        inputPanel.setBackground(Color.WHITE);

        int panelWidth = 600;
        int fieldWidth = 250;
        int fieldHeight = 40;
        int labelWidth = 100;
        int startX = (panelWidth - (labelWidth + fieldWidth)) / 2;

        // 아이디 라벨과 아이디 입력 필드
        JLabel idLabel = new JLabel("아이디");
        idLabel.setBounds(startX, 20, labelWidth, fieldHeight);
        idLabel.setFont(new Font("나눔고딕", Font.BOLD, 18));
        JTextField idField = new JTextField();
        idField.setBounds(startX + labelWidth, 20, fieldWidth, fieldHeight);
        idField.setFont(new Font("나눔고딕", Font.PLAIN, 18));

        // 비밀번호 라벨과 비밀번호 입력 필드
        JLabel pwLabel = new JLabel("비밀번호");
        pwLabel.setBounds(startX, 70, labelWidth, fieldHeight);
        pwLabel.setFont(new Font("나눔고딕", Font.BOLD, 18));
        JPasswordField pwField = new JPasswordField();
        pwField.setBounds(startX + labelWidth, 70, fieldWidth, fieldHeight);
        pwField.setFont(new Font("나눔고딕", Font.PLAIN, 18));

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(pwLabel);
        inputPanel.add(pwField);

        add(inputPanel, BorderLayout.CENTER);

        //================= 하단 버튼 패널 ====================
        JPanel btnPanel = new JPanel(null);
        btnPanel.setPreferredSize(new Dimension(600, 170));
        btnPanel.setBackground(Color.WHITE);

        int buttonWidth = 200;
        int buttonHeight = 60;
        int buttonY = 20;
        int gap = 30;
        int buttonsTotalWidth = buttonWidth * 2 + gap;
        int btnStartX = (panelWidth - buttonsTotalWidth) / 2;

        // 로그인 버튼
        JButton btnLogin = new JButton("로그인");
        btnLogin.setBounds(btnStartX, buttonY, buttonWidth, buttonHeight);
        btnLogin.setFont(new Font("나눔고딕", Font.BOLD, 22));
        
        // 뒤로가기 버튼
        JButton btnBack = new JButton("뒤로가기");
        btnBack.setBounds(btnStartX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight);
        btnBack.setFont(new Font("나눔고딕", Font.BOLD, 22));

        btnPanel.add(btnLogin);
        btnPanel.add(btnBack);

        add(btnPanel, BorderLayout.SOUTH);

        
        //=======================버튼 이벤트========================
        btnLogin.addActionListener(e -> {
            String inputId = idField.getText();
            String inputPass = new String(pwField.getPassword());

            if (inputId.isEmpty() || inputPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 모두 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            MemberDTO loginMember = memberDAO.loginMember(inputId, inputPass);

            if(loginMember == null) {
                JOptionPane.showMessageDialog(this, "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다!!", "로그인 실패", JOptionPane.WARNING_MESSAGE);
            } else {
                System.out.println("로그인 성공");
                System.out.println(loginMember.getMem_name());
                if(loginMember.getMem_admin() == 1) {//관리자
                    JOptionPane.showMessageDialog(this, "사장님 환영합니다!!", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                    FrameBase.getInstance(new PanelAdmin(loginMember));
                }
                else if(loginMember.getMem_admin() == 0){//사용자
                    JOptionPane.showMessageDialog(this, loginMember.getMem_name() + "님 환영합니다!!", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                    if(loginMember.getMem_time() == 0) {
                        JOptionPane.showMessageDialog(this, "시간을 충전해주세요!!", "시간충전", JOptionPane.INFORMATION_MESSAGE);
                        FrameBase.getInstance(new PanelChargeWrapper(loginMember)); // Navigate to PanelChargeWrapper
                    }
                    else{
                        FrameBase.getInstance(new PanelUser(loginMember));
                    }
                }
            }
        });

        // 뒤로가기 버튼 이벤트
        btnBack.addActionListener(e -> {
            FrameBase.getInstance(new PanelStart());
        });
    }
}