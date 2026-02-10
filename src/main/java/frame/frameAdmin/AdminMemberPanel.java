package frame.frameAdmin;

import db.MemberDAO;
import vo.MemberDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminMemberPanel extends JPanel {



    private MemberDAO mDao;
    private JTable memberTable;
    private DefaultTableModel tableModel;

    public AdminMemberPanel(MemberDAO mDao) {
        
        this.mDao = mDao;
        setLayout(new BorderLayout());

        // 컬럼 이름 설정
        String[] cols = {"저장대기", "번호", "아이디", "이름", "잔여시간(분)","나이", "보유머니"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 2; // ID까지 변경을 제한함
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                // 원래 있던 값 가져오기
                Object oldValue = getValueAt(row, column);

                // 값이 실제로 변했을 때만 실행
                if (column != 0 && oldValue != null && !oldValue.equals(aValue)) {
                    super.setValueAt(aValue, row, column); // 값 먼저 변경
                    super.setValueAt("!", row, 0);         // 0번 컬럼(상태)에 ! 표시
                } else {
                    super.setValueAt(aValue, row, column);
                }
            }
        };
        
        memberTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(memberTable);

        JPanel bottomPanel = new JPanel();
        JButton btnRefresh = new JButton("회원목록 새로고침");
        JButton btnSave = new JButton("수정사항 저장");
        JButton btnPass = new JButton("패스워드 변경");
        JButton btnRemove = new JButton("회원정보 삭제");

        // 테이블 렌더러 설정 (색상 변경)
        memberTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // 1. 기본 설정: 글자색은 항상 검정
                c.setForeground(Color.BLACK);
                Font baseFont = table.getFont();
                
                // 2. 상태값 확인
                Object statusObj = table.getModel().getValueAt(row, 0);
                String status = (statusObj != null) ? statusObj.toString() : "";
                
                // 3. 조건별 스타일 적용
                if (status.equals("!")) {
                    // [수정된 행] 배경: 연분홍, 폰트: BOLD
                    c.setBackground(new Color(255, 230, 230));
                    c.setFont(baseFont.deriveFont(Font.BOLD));
                } else {
                    // [일반 행] 폰트: PLAIN (반드시 명시해야 다른 행에 영향 안 줌)
                    c.setFont(baseFont.deriveFont(Font.PLAIN));
                    
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        // 새로고침
        btnRefresh.addActionListener(e -> {
            refreshMemberData();
        });
        // 수정사항 저장
        btnSave.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "행을 선택하세요..");
                return;
            }
            try {
                int idx = (int) tableModel.getValueAt(row, 1);
                String name = tableModel.getValueAt(row, 3).toString(); 
                int time = Integer.parseInt(tableModel.getValueAt(row, 4).toString());
                int age = Integer.parseInt(tableModel.getValueAt(row, 5).toString());
                int money = Integer.parseInt(tableModel.getValueAt(row, 6).toString());
                
                MemberDTO m = new MemberDTO();
                m.setMem_idx(idx); 
                m.setMem_name(name);
                m.setMem_time(time);
                m.setMem_age(age);
                m.setMem_money(money);

                int result = mDao.updateMember(m);

                    if (result > 0) {
                        JOptionPane.showMessageDialog(null, "DB 저장 성공!");
                        tableModel.setValueAt("", row, 0); 
                    } else {
                        JOptionPane.showMessageDialog(null, "저장 실패!");
                    }
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, "데이터 형식이 맞지 않습니다.");
                    e1.printStackTrace();
            }

        });
        //패스워드 변경
        btnPass.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "행을 선택하세요..");
                return;
            }
            JPasswordField pf = new JPasswordField();
            int action = JOptionPane.showConfirmDialog(null, pf, "새 비밀번호 입력", JOptionPane.OK_CANCEL_OPTION);

            if (action == JOptionPane.OK_OPTION) {
                String newPass = new String(pf.getPassword());
                int idx = (int) tableModel.getValueAt(row, 1);

                MemberDTO m = new MemberDTO();
                m.setMem_idx(idx);
                m.setMem_pass(newPass);
                int result = mDao.updatePass(idx, newPass); 
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "비밀번호가 변경되었습니다.");
                }
            }
        });
        // 회원정보 삭제
        btnRemove.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "행을 선택하세요..");
                return;
            }
            int idx = (int) tableModel.getValueAt(row, 1);
            String name = (String) tableModel.getValueAt(row, 3);

            int confirm = JOptionPane.showConfirmDialog(null, name + " 회원을 정말 지우시겠습니까?","회원 삭제", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
            
                // DB 작업 실행
            int result = mDao.deleteMember(idx); 

            if (result > 0) {
                JOptionPane.showMessageDialog(null, name + " 회원이 성공적으로 삭제되었습니다.");
                refreshMemberData(); // UI 갱신
            } else {
                JOptionPane.showMessageDialog(null, "삭제 실패!..");
            }
        }
        });
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnSave);
        bottomPanel.add(btnPass);
        bottomPanel.add(btnRemove);

        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH); 
        
        refreshMemberData(); // 초기 데이터 로드
    }

    public void refreshMemberData() {
        tableModel.setRowCount(0);
        ArrayList<MemberDTO> list = mDao.getAllMembers();
        for (MemberDTO m : list) {
            Object[] data = {
                "", m.getMem_idx(), m.getMem_id(), m.getMem_name(), m.getMem_time(), m.getMem_age(), m.getMem_money()
            };
            tableModel.addRow(data);
        }
    }
    
}