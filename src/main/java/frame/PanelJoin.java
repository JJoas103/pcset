package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.mindrot.jbcrypt.BCrypt;
import vo.MemberDTO;
import db.MemberDAO;
public class PanelJoin extends JPanel {

    public PanelJoin() {
        // 패널 기본 설정 (PanelLogin과 동일한 스타일)
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 800));
        setBackground(Color.WHITE);

        MemberDAO memberDAO = new MemberDAO();

        //============== 상단 이미지 패널 ==============================
        // 회원가입 화면에 어울리는 이미지를 사용합니다 (예: "img/joinImage.png")
        // 이미지가 없을 경우를 대비해 예외 처리를 포함할 수 있습니다.
        ImageIcon originalIcon = new ImageIcon("src\\main\\java\\img\\loginImage.png");
        Image scaledImg = originalIcon.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(imgLabel, BorderLayout.CENTER);
        imgPanel.setBackground(Color.WHITE);

        add(imgPanel, BorderLayout.NORTH);

        //============= 중단 입력 패널 ==============================
        JPanel inputPanel = new JPanel(null); // 절대 위치 지정을 위해 null 레이아웃 사용
        inputPanel.setPreferredSize(new Dimension(600, 300)); // 패널 높이 축소
        inputPanel.setBackground(Color.WHITE);

        // 레이아웃 값 재조정 (전체적으로 크기 축소)
        int panelWidth = 600;
        int fieldHeight = 35; // 필드 높이 축소
        int labelWidth = 120;
        int fieldWidth = 180; // 필드 너비 축소
        int btnWidth = 90;    // 버튼 너비 축소
        int btnGap = 10;
        int startY = 30;      // 시작 Y좌표
        int gapY = 20;        // 세로 간격 축소

        // 중앙 정렬을 위한 시작 X 좌표 계산
        int totalContentWidth = labelWidth + fieldWidth + btnGap + btnWidth;
        int startXLabel = (panelWidth - totalContentWidth) / 2;
        int startXField = startXLabel + labelWidth;
        int startXButton = startXField + fieldWidth + btnGap;
        
        int currentY = startY;

        // 1. 아이디 (ID)
        JLabel idLabel = new JLabel("아이디");
        idLabel.setBounds(startXLabel, currentY, labelWidth, fieldHeight);
        idLabel.setFont(new Font("나눔고딕", Font.BOLD, 16)); // 폰트 크기 조정
        inputPanel.add(idLabel);

        JTextField idField = new JTextField();
        idField.setBounds(startXField, currentY, fieldWidth, fieldHeight);
        idField.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        inputPanel.add(idField);

        JButton btnCheckId = new JButton("중복확인");
        btnCheckId.setBounds(startXButton, currentY, btnWidth, fieldHeight);
        btnCheckId.setFont(new Font("나눔고딕", Font.BOLD, 12));
        btnCheckId.setMargin(new Insets(0, 0, 0, 0));
        inputPanel.add(btnCheckId);
        currentY += fieldHeight + gapY;

        // 2. 비밀번호 (Password)
        JLabel passLabel = new JLabel("비밀번호");
        passLabel.setBounds(startXLabel, currentY, labelWidth, fieldHeight);
        passLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        inputPanel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(startXField, currentY, fieldWidth + btnGap + btnWidth, fieldHeight);
        passField.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        inputPanel.add(passField);
        currentY += fieldHeight + gapY;

        // 3. 비밀번호 확인 (Password Confirmation)
        JLabel passConfirmLabel = new JLabel("비밀번호 확인");
        passConfirmLabel.setBounds(startXLabel, currentY, labelWidth, fieldHeight);
        passConfirmLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        inputPanel.add(passConfirmLabel);

        JPasswordField passConfirmField = new JPasswordField();
        passConfirmField.setBounds(startXField, currentY, fieldWidth + btnGap + btnWidth, fieldHeight);
        passConfirmField.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        inputPanel.add(passConfirmField);
        currentY += fieldHeight + gapY;

        // 4. 이름 (Name)
        JLabel nameLabel = new JLabel("이름");
        nameLabel.setBounds(startXLabel, currentY, labelWidth, fieldHeight);
        nameLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        inputPanel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(startXField, currentY, fieldWidth + btnGap + btnWidth, fieldHeight);
        nameField.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        inputPanel.add(nameField);
        currentY += fieldHeight + gapY;

        // 5. 나이 (Age)
        JLabel ageLabel = new JLabel("나이");
        ageLabel.setBounds(startXLabel, currentY, labelWidth, fieldHeight);
        ageLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        inputPanel.add(ageLabel);

        JTextField ageField = new JTextField();
        ageField.setBounds(startXField, currentY, fieldWidth + btnGap + btnWidth, fieldHeight);
        ageField.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        inputPanel.add(ageField);
        
        add(inputPanel, BorderLayout.CENTER);

        //================= 하단 버튼 패널 ====================
        JPanel btnPanel = new JPanel(null);
        btnPanel.setPreferredSize(new Dimension(600, 150));
        btnPanel.setBackground(Color.WHITE);

        int buttonWidth = 200;
        int buttonHeight = 60;
        int buttonY = 20;
        int gapX = 30;
        int buttonsTotalWidth = buttonWidth * 2 + gapX;
        int btnStartX = (panelWidth - buttonsTotalWidth) / 2;

        // 회원가입 버튼
        JButton btnJoin = new JButton("회원가입");
        btnJoin.setBounds(btnStartX, buttonY, buttonWidth, buttonHeight);
        btnJoin.setFont(new Font("나눔고딕", Font.BOLD, 22));
        
        // 뒤로가기 버튼
        JButton btnBack = new JButton("뒤로가기");
        btnBack.setBounds(btnStartX + buttonWidth + gapX, buttonY, buttonWidth, buttonHeight);
        btnBack.setFont(new Font("나눔고딕", Font.BOLD, 22));

        btnPanel.add(btnJoin);
        btnPanel.add(btnBack);

        add(btnPanel, BorderLayout.SOUTH);

        //ID필드 상태에 따른 회원가입 버튼 상태
        idField.getDocument().addDocumentListener(new DocumentListener() {
            //ID 필드 변경 감지 - 값이 변경되면 회원가입 버튼 비활성화
            @Override
            public void insertUpdate(DocumentEvent e) {
                btnJoin.setEnabled(false);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                btnJoin.setEnabled(false);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                btnJoin.setEnabled(false);
            }
        });

        btnCheckId.addActionListener(e -> {
            String inputId = idField.getText();

            if(inputId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID를 입력하세요!!", "입력오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean check = memberDAO.checkId(inputId);//중복확인

            if(check) {//중복된 ID면 경고 메시지 출력
                JOptionPane.showConfirmDialog(this, "이미 사용중인 ID입니다!!", "중복ID", JOptionPane.WARNING_MESSAGE);
                return;
            }else{
                JOptionPane.showConfirmDialog(this, "사용 가능한 ID입니다!!", "중복ID", JOptionPane.INFORMATION_MESSAGE);
                btnJoin.setEnabled(true);
            }
        });
        btnJoin.addActionListener(e -> {
            String inputId = idField.getText();//입력한 아이디 
            String inputPass = new String(passField.getPassword());//입력한 비밀번호
            String inputPass2 = new String(passConfirmField.getPassword());//입력한 비밀번호확인 값
            String inputName = nameField.getText();//입력한 이름
            int inputAge = Integer.parseInt(ageField.getText());//입력한 나이

            // 입력란 공란 이슈
            if(inputId.isEmpty() || inputName.isEmpty() || inputPass.isEmpty() || inputPass2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요!!", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 비밀번호 불일치
            if(!inputPass.equals(inputPass2)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다!!", "입력 오류", JOptionPane.WARNING_MESSAGE);
            }

            // 비밀번호 해싱
            String hashedPassword = BCrypt.hashpw(inputPass, BCrypt.gensalt());

            // 회원가입
            JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.", "회원가입", JOptionPane.INFORMATION_MESSAGE);
            MemberDTO joinMember = new MemberDTO(inputId, hashedPassword, inputName, inputAge);
            memberDAO.insertMember(joinMember);//DB에 회원 저장
            FrameBase.getInstance(new PanelLogin());//회원가입 완료후 로그인 
        });
        // 뒤로가기 버튼 이벤트
        btnBack.addActionListener(e -> {
            FrameBase.getInstance(new PanelStart());
        });
    }
}
