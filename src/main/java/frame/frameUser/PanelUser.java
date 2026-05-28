package frame.frameUser;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import frame.FrameBase;
import frame.PanelStart;

import javax.swing.JButton;

import java.awt.*;
import vo.MemberDTO;
public class PanelUser extends JPanel{
    
    public PanelUser(MemberDTO loginMember) {



        // 패널 기본 설정
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 800));
        setBackground(Color.WHITE);

        //============== 상단 이미지 패널 (NORTH) ==============================
        ImageIcon originalIcon = new ImageIcon("src\\main\\java\\img\\loginImage.png"); 
        Image scaledImg = originalIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH); 
        JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(imgLabel, BorderLayout.CENTER);
        imgPanel.setBackground(Color.WHITE); 

        add(imgPanel, BorderLayout.NORTH);

        //============= 중앙 빈 패널 (CENTER) ==============================
        // 상단 이미지와 하단 버튼 사이의 여백 및 중앙 영역 채우기
        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(600, 100)); 
        centerPanel.setBackground(Color.WHITE);
        add(centerPanel, BorderLayout.CENTER);


        //============= 하단 버튼 패널 (SOUTH) ==============================
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(1, 3, 10, 10)); 
        btnPanel.setPreferredSize(new Dimension(600, 300)); 
        btnPanel.setBackground(Color.WHITE);

        Font buttonFont = new Font("나눔고딕", Font.BOLD, 20);
        Dimension buttonSize = new Dimension(150, 60);

        //버튼 1
        JButton button1 = new JButton("좌석 선택");
        button1.setFont(buttonFont);
        button1.setPreferredSize(buttonSize); 
        
        button1.setBackground(new Color(240, 240, 240));
        button1.setForeground(Color.BLACK);
        button1.setFocusPainted(false);
        button1.setBorderPainted(false);
        
        btnPanel.add(button1);
        
        //버튼 2
        JButton button2 = new JButton("음식 주문, 시간추가");
        button2.setFont(buttonFont);
        button2.setPreferredSize(buttonSize); 
        button2.setBackground(new Color(240, 240, 240)); 
        button2.setForeground(Color.BLACK);
        button2.setFocusPainted(false);
        button2.setBorderPainted(false);
        
        btnPanel.add(button2);

        //버튼 3
        JButton button3 = new JButton("로그아웃");
        button3.setFont(buttonFont);
        button3.setPreferredSize(buttonSize); 
        button3.setBackground(new Color(240, 240, 240));
        button3.setForeground(Color.BLACK);
        button3.setFocusPainted(false);
        button3.setBorderPainted(false);
        
        btnPanel.add(button3);
        
        add(btnPanel, BorderLayout.SOUTH);

        if(loginMember.getMem_time() == 0) {
            button1.setEnabled(false);
            button2.setEnabled(false);
            button3.setEnabled(false);
        }
        //좌석선택
        button1.addActionListener(e -> {
           UserSeat userView = new UserSeat(loginMember); // UserSeat 인스턴스 생성
           userView.setVisible(true); // 새 창을 보이도록 설정
        });
        //음식 주문
        button2.addActionListener(e -> {
            FrameBase.getInstance(new PanelChargeWrapper(loginMember)); // 새로운 PcCafeGUI 창을 띄움 (Now PanelChargeWrapper)
        });
        //로그아웃
        button3.addActionListener(e -> {
            FrameBase.getInstance(new PanelStart());
        });
    }
}