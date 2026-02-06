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
        // 이용시간가격변경, 좌석확인, 로그아웃
        //버튼 1
        JButton button1 = new JButton("이용시간가격변경");
        button1.setFont(buttonFont);
        // setPreferredSize는 GridLayout에서는 직접적인 영향이 적지만, 일관성을 위해 유지
        button1.setPreferredSize(buttonSize); 
        // 배경색, 포그라운드 색상 등 필요에 따라 추가
        button1.setBackground(new Color(240, 240, 240)); // 연한 회색 기본
        button1.setForeground(Color.BLACK);
        button1.setFocusPainted(false);
        button1.setBorderPainted(false);
        
        btnPanel.add(button1);

        //버튼 2
        JButton button2 = new JButton("좌석확인");
        button2.setFont(buttonFont);
        // setPreferredSize는 GridLayout에서는 직접적인 영향이 적지만, 일관성을 위해 유지
        button2.setPreferredSize(buttonSize); 
        // 배경색, 포그라운드 색상 등 필요에 따라 추가
        button2.setBackground(new Color(240, 240, 240)); // 연한 회색 기본
        button2.setForeground(Color.BLACK);
        button2.setFocusPainted(false);
        button2.setBorderPainted(false);
        
        btnPanel.add(button2);

        //버튼 4 (이전 button6)
        JButton button4 = new JButton("로그아웃");
        button4.setFont(buttonFont);
        // setPreferredSize는 GridLayout에서는 직접적인 영향이 적지만, 일관성을 위해 유지
        button4.setPreferredSize(buttonSize); 
        // 배경색, 포그라운드 색상 등 필요에 따라 추가
        button4.setBackground(new Color(240, 240, 240)); // 연한 회색 기본
        button4.setForeground(Color.BLACK);
        button4.setFocusPainted(false);
        button4.setBorderPainted(false);
        
        btnPanel.add(button4);
        
        
        
        add(btnPanel, BorderLayout.SOUTH);

        // 로그인 멤버 정보 활용 예시 (현재는 사용하지 않음)
        // if (loginMember != null) {
        //     System.out.println("User Panel loaded for: " + loginMember.getMemberName());
        // }
        // 이용시간가격변경, 좌석확인, 로그아웃
        button1.addActionListener(e -> {
            new AdminView();
        });
        //사장의 좌석상태 확인
        button2.addActionListener(e -> {
           //FrameBase.getInstance(new AdminView());
           new AdminSeat(loginMember);
        });
        
        //뒤로가기
        button4.addActionListener(e -> {
            FrameBase.getInstance(new PanelStart());
        }); 
    }
}
//사장 관리자페이지AdminView()의 1페이지 createMemberPanel, createOrderPanel확인필요
// 에서 주문확인, 회원관리1페이지의 나이 반영이 안됨이슈 db나
// 좌석현황 합칠지 어떻게할지 다시 확인 AdminView()와 AdminSeat()이 겹침
