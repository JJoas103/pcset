package frame.frameUser;

import javax.swing.*;

import db.MemberDAO;
import vo.MemberDTO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MoneyPanel extends JPanel {
    private PanelChargeWrapper parentPanel;
    private JTextField amountField;
    private JButton chargeButton;

    public MoneyPanel(PanelChargeWrapper parentPanel) { // Modified constructor
        MemberDAO memberDAO = new MemberDAO();
        this.parentPanel = parentPanel;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("금액 충전", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30)); 
        add(titleLabel, BorderLayout.NORTH);

        // inputPanel을 BoxLayout으로 변경하여 요소들을 세로로 배치
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); 

        // "충전할 금액" 라벨과 입력 필드를 담을 패널
        JPanel amountInputRow = new JPanel();
        amountInputRow.setLayout(new BoxLayout(amountInputRow, BoxLayout.Y_AXIS)); 

        JLabel amountLabel = new JLabel("충전할 금액: ");
        amountLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); 
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        amountInputRow.add(amountLabel);

        amountInputRow.add(Box.createVerticalStrut(5));

        amountField = new JTextField(10);
        amountField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        amountField.setMaximumSize(amountField.getPreferredSize());
        amountField.setAlignmentX(Component.CENTER_ALIGNMENT); 
        // Enter 키를 눌렀을 때 충전 버튼 클릭하도록 추가
        amountField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chargeButton.doClick(); // 충전 버튼 클릭 이벤트 발생
            }
        });
        amountInputRow.add(amountField);

        chargeButton = new JButton("충전하기");
        chargeButton.setFont(new Font("맑은 고딕", Font.BOLD, 16)); 
        chargeButton.setAlignmentX(Component.CENTER_ALIGNMENT); 
        chargeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int amount = Integer.parseInt(amountField.getText());
                    if (amount > 0) {
                        // Fetch fresh MemberDTO before charging to ensure correct mem_idx and money
                        MemberDTO freshMember = parentPanel.dao.getMember(parentPanel.getCurrentLoginMember().getMem_idx());
                        if (freshMember == null) {
                            JOptionPane.showMessageDialog(parentPanel, "회원 정보를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        // 금액 충전 로직 (DAO 호출 등)
                        boolean success = memberDAO.chargeMoney(freshMember, amount); // Use freshMember
                        if (success) {
                            JOptionPane.showMessageDialog(parentPanel, amount + "원 충전 완료!");
                            parentPanel.refreshUserInfo(); // 상단 정보 갱신
                        } else {
                            JOptionPane.showMessageDialog(parentPanel, "금액 충전 실패!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(parentPanel, "양수 값을 입력하세요.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parentPanel, "유효한 금액을 입력하세요.");
                }
            }
        });

        // 요소들을 inputPanel에 추가 (세로로 쌓임)
        inputPanel.add(amountInputRow);
        inputPanel.add(Box.createVerticalStrut(10)); 
        inputPanel.add(chargeButton);
        inputPanel.add(Box.createVerticalStrut(10)); 

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(inputPanel);
        add(wrapperPanel, BorderLayout.CENTER);
    }
}
