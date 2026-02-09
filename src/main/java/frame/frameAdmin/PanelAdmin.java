package frame.frameAdmin;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import frame.FrameBase;
import frame.PanelStart;

import java.awt.*;
import vo.MemberDTO;

public class PanelAdmin extends JPanel{
    
    public PanelAdmin(MemberDTO loginMember) {
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
        // 이용시간가격변경, 좌석확인, 로그아웃
        //버튼 1
        JButton button1 = new JButton("이용시간가격변경");
        button1.setFont(buttonFont);
        button1.setPreferredSize(buttonSize); 
        button1.setBackground(new Color(240, 240, 240));
        button1.setForeground(Color.BLACK);
        button1.setFocusPainted(false);
        button1.setBorderPainted(false);
        
        btnPanel.add(button1);

        //버튼 2
        JButton button2 = new JButton("좌석확인");
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

        // 이용시간가격변경, 좌석확인, 로그아웃
        button1.addActionListener(e -> {
            new AdminView();
        });
        //사장의 좌석상태 확인
        button2.addActionListener(e -> {
           new AdminSeat(loginMember);
        });
        
        //뒤로가기
        button3.addActionListener(e -> {
            FrameBase.getInstance(new PanelStart());
        }); 
    }
}
// 좌석현황 합칠지 어떻게할지 다시 확인 AdminView()와 AdminSeat()이 겹침
