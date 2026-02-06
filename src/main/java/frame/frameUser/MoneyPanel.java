package frame.frameUser;

import javax.swing.*;

import db.MemberDAO;
import vo.MemberDTO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MoneyPanel extends JPanel {
    private PcCafeGUI mainFrame;
    MemberDTO loginMember;
    private JTextField amountField;
    private JButton chargeButton;

    public MoneyPanel(PcCafeGUI mainFrame, MemberDTO loginMember) {
        MemberDAO memberDAO = new MemberDAO();
        this.loginMember = loginMember;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("금액 충전", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30)); // 글자 크기 30으로 키움
        add(titleLabel, BorderLayout.NORTH);

        // inputPanel을 BoxLayout으로 변경하여 요소들을 세로로 배치
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // 세로 정렬

        // "충전할 금액" 라벨과 입력 필드를 담을 패널
        JPanel amountInputRow = new JPanel();
        amountInputRow.setLayout(new BoxLayout(amountInputRow, BoxLayout.Y_AXIS)); // 세로 정렬

        JLabel amountLabel = new JLabel("충전할 금액: ");
        amountLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); // 글자 크기 16
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬
        amountInputRow.add(amountLabel);

        amountInputRow.add(Box.createVerticalStrut(5)); // 라벨과 입력 필드 사이 여백

        amountField = new JTextField(10);
        amountField.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); // 글자 크기 16
        amountField.setMaximumSize(amountField.getPreferredSize()); // 텍스트 필드 크기 고정 (BoxLayout에서 필요)
        amountField.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬
        // Enter 키를 눌렀을 때 충전 버튼 클릭하도록 추가
        amountField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chargeButton.doClick(); // 충전 버튼 클릭 이벤트 발생
            }
        });
        amountInputRow.add(amountField);

        chargeButton = new JButton("충전하기");
        chargeButton.setFont(new Font("맑은 고딕", Font.BOLD, 16)); // 글자 크기 16
        chargeButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 버튼을 중앙으로 정렬
        chargeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int amount = Integer.parseInt(amountField.getText());
                    if (amount > 0) {
                        // 금액 충전 로직 (DAO 호출 등)
                        // Assuming PcCafeDAO has a chargeMoney method
                        boolean success = memberDAO.chargeMoney(loginMember, amount);
                        //mainFrame.dao.chargeMoney(loginMember.getMem_id(), amount);
                        if (success) {
                            JOptionPane.showMessageDialog(mainFrame, amount + "원 충전 완료!");
                            mainFrame.refreshUserInfo(); // 상단 정보 갱신
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "금액 충전 실패!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "양수 값을 입력하세요.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainFrame, "유효한 금액을 입력하세요.");
                }
            }
        });

        // 요소들을 inputPanel에 추가 (세로로 쌓임)
        inputPanel.add(amountInputRow);
        inputPanel.add(Box.createVerticalStrut(10)); // 여백 추가
        inputPanel.add(chargeButton);
        inputPanel.add(Box.createVerticalStrut(10)); // 여백 추가

        // inputPanel을 중앙에 배치하기 위한 wrapper panel (BorderLayout 사용)
        JPanel wrapperPanel = new JPanel(new GridBagLayout()); // GridBagLayout으로 중앙 정렬
        wrapperPanel.add(inputPanel);
        add(wrapperPanel, BorderLayout.CENTER);
    }
}
