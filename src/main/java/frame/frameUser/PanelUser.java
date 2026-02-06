package frame.frameUser;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import frame.FrameBase;
import frame.PanelStart;
import frame.frameAdmin.AdminView;

import javax.swing.JButton;

import java.awt.*;
import vo.MemberDTO;
public class PanelUser extends JPanel{
    
    public PanelUser(MemberDTO loginMember) {



        // 패널 기본 설정 (PanelLogin/Join과 일관성 유지)
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 800));
        setBackground(Color.WHITE);

        //============== 상단 이미지 패널 (NORTH) ==============================
        ImageIcon originalIcon = new ImageIcon("src\\main\\java\\img\\loginImage.png"); // 혹은 img/userPanelTop.png 등으로 변경 가능
        Image scaledImg = originalIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH); // 높이 조정
        JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(imgLabel, BorderLayout.CENTER);
        imgPanel.setBackground(Color.WHITE); // 배경색 설정

        add(imgPanel, BorderLayout.NORTH);

        //============= 중앙 빈 패널 (CENTER) ==============================
        // 상단 이미지와 하단 버튼 사이의 여백 및 중앙 영역 채우기
        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(600, 100)); // 높이 할당
        centerPanel.setBackground(Color.WHITE);
        add(centerPanel, BorderLayout.CENTER);


        //============= 하단 버튼 패널 (SOUTH) ==============================
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(1, 3, 10, 10)); // 1행 3열 그리드, 10px 간격
        btnPanel.setPreferredSize(new Dimension(600, 300)); // 버튼 패널 높이 할당
        btnPanel.setBackground(Color.WHITE);

        // 버튼 스타일링 헬퍼 함수
        // 다른 패널의 버튼 스타일과 일관성을 유지합니다.
        Font buttonFont = new Font("나눔고딕", Font.BOLD, 20);
        Dimension buttonSize = new Dimension(150, 60);

        //버튼 1 (이전 button2)
        JButton button1 = new JButton("좌석 선택");
        button1.setFont(buttonFont);
        // setPreferredSize는 GridLayout에서는 직접적인 영향이 적지만, 일관성을 위해 유지
        button1.setPreferredSize(buttonSize); 
        // 배경색, 포그라운드 색상 등 필요에 따라 추가
        button1.setBackground(new Color(240, 240, 240)); // 연한 회색 기본
        button1.setForeground(Color.BLACK);
        button1.setFocusPainted(false);
        button1.setBorderPainted(false);
        
        btnPanel.add(button1);
        
        //버튼 2 (이전 button3)
        JButton button2 = new JButton("음식 주문, 시간추가");
        button2.setFont(buttonFont);
        // setPreferredSize는 GridLayout에서는 직접적인 영향이 적지만, 일관성을 위해 유지
        button2.setPreferredSize(buttonSize); 
        // 배경색, 포그라운드 색상 등 필요에 따라 추가
        button2.setBackground(new Color(240, 240, 240)); // 연한 회색 기본
        button2.setForeground(Color.BLACK);
        button2.setFocusPainted(false);
        button2.setBorderPainted(false);
        
        btnPanel.add(button2);

        //버튼 3 (이전 button4)
        JButton button3 = new JButton("로그아웃");
        button3.setFont(buttonFont);
        // setPreferredSize는 GridLayout에서는 직접적인 영향이 적지만, 일관성을 위해 유지
        button3.setPreferredSize(buttonSize); 
        // 배경색, 포그라운드 색상 등 필요에 따라 추가
        button3.setBackground(new Color(240, 240, 240)); // 연한 회색 기본
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
        //잔액충전 (삭제됨)
        //좌석선택
        button1.addActionListener(e -> {
           UserView userView = new UserView(loginMember); // UserView 인스턴스 생성
           userView.setVisible(true); // 새 창을 보이도록 설정
        });
        //음식 주문
        button2.addActionListener(e -> {
            new PcCafeGUI(loginMember); // 새로운 PcCafeGUI 창을 띄움
        });
        //로그아웃
        button3.addActionListener(e -> {
            FrameBase.getInstance(new PanelStart());
        });
    }
} 
//좌석선택, 시간충전(시간이 없을경우)를 선행해야 다른 메뉴 선택 가능

//