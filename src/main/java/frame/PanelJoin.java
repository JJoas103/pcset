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
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 800));
        setBackground(Color.WHITE);

        MemberDAO memberDAO = new MemberDAO();

        //============== 상단 이미지 패널 ==============================
        ImageIcon originalIcon = new ImageIcon("src\\main\\java\\img\\loginImage.png");
        Image scaledImg = originalIcon.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(imgLabel, BorderLayout.CENTER);
        imgPanel.setBackground(Color.WHITE);

        add(imgPanel, BorderLayout.NORTH);

        //============= 중단 입력 패널 ==============================
        JPanel inputPanel = new JPanel(null); 
        inputPanel.setPreferredSize(new Dimension(600, 300)); 
        inputPanel.setBackground(Color.WHITE);

        int panelWidth = 600;
        int fieldHeight = 35; 
        int labelWidth = 120;
        int fieldWidth = 180; 
        int btnWidth = 90;   
        int btnGap = 10;
        int startY = 30;    
        int gapY = 20;      

        int totalContentWidth = labelWidth + fieldWidth + btnGap + btnWidth;
        int startXLabel = (panelWidth - totalContentWidth) / 2;
        int startXField = startXLabel + labelWidth;
        int startXButton = startXField + fieldWidth + btnGap;
        
        int currentY = startY;

        // 1. 아이디 (ID)
        JLabel idLabel = new JLabel("아이디");
        idLabel.setBounds(startXLabel, currentY, labelWidth, fieldHeight);
        idLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
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

            boolean check = memberDAO.checkId(inputId);

            if(check) {
                JOptionPane.showConfirmDialog(this, "이미 사용중인 ID입니다!!", "중복ID", JOptionPane.WARNING_MESSAGE);
                return;
            }else{
                JOptionPane.showConfirmDialog(this, "사용 가능한 ID입니다!!", "중복ID", JOptionPane.INFORMATION_MESSAGE);
                btnJoin.setEnabled(true);
            }
        });
        btnJoin.addActionListener(e -> {
            String inputId = idField.getText();
            String inputPass = new String(passField.getPassword());
            String inputPass2 = new String(passConfirmField.getPassword());
            String inputName = nameField.getText();
            String ageText = ageField.getText(); 

            // 입력란 공란 이슈
            if(inputId.isEmpty() || inputName.isEmpty() || inputPass.isEmpty() || inputPass2.isEmpty() || ageText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요!!", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int inputAge;
            try {
                inputAge = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "나이는 숫자로 입력하세요!!", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 비밀번호 불일치
            if(!inputPass.equals(inputPass2)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다!!", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return; 
            }

            // 비밀번호 암호화
            String hashedPassword = BCrypt.hashpw(inputPass, BCrypt.gensalt());

            // 회원가입
            JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.", "회원가입", JOptionPane.INFORMATION_MESSAGE);
            MemberDTO joinMember = new MemberDTO(inputId, hashedPassword, inputName, inputAge);
            memberDAO.insertMember(joinMember);
            FrameBase.getInstance(new PanelLogin());
        });
        // 뒤로가기 버튼 이벤트
        btnBack.addActionListener(e -> {
            FrameBase.getInstance(new PanelStart());
        });
    }
}
