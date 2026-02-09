package frame.frameAdmin;

import java.awt.*;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import db.LogDAO;
import db.MemberDAO;
import db.OrderDAO;
import db.SeatDAO;
import db.TimeDAO;
import db.AdminDAO;
import db.FoodDAO;
import vo.LogDTO;
import vo.MemberDTO;
import vo.SeatDTO;

public class AdminView extends JDialog {
    
    
    MemberDAO mDao = new MemberDAO();
    SeatDAO sDao = new SeatDAO();
    LogDAO lDao = new LogDAO();
    FoodDAO fDao = new FoodDAO();
    TimeDAO tDao = new TimeDAO();
    OrderDAO oDao = new OrderDAO();
    JTabbedPane tabPane;
    JTable memberTable;
    DefaultTableModel tableModel;
    JPanel seatPanel;
    JLabel revenueLabel;

    public AdminView() {
        setTitle("PC방 관리자 모드");
        setSize(1200, 800);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null); 

        
        tabPane = new JTabbedPane();
        
        tabPane.addTab("1. 회원 관리", createMemberPanel());
        tabPane.addTab("2. 좌석 현황", createSeatPanel());
        tabPane.addTab("3. 매출 조회", createRevenuePanel());
        tabPane.addTab("4. 음식 관리", AdminTestGUI.createFoodPanel(new FoodDAO())); 
        tabPane.addTab("5. 주문 관리", AdminTestGUI.createOrderPanel(oDao));
        tabPane.addTab("6. 시간/요금 관리", AdminTestGUI.createTimePanel(new TimeDAO()));

        add(tabPane);
        setVisible(true);
        
        refreshMemberData();
        refreshSeatData();
        
    }

    public void refreshMemberData() {
        
        tableModel.setRowCount(0);
        
        
        ArrayList<MemberDTO> list = mDao.getAllMembers();
        
                for (MemberDTO m : list) {
            Object[] data = {
                m.getMem_idx(), m.getMem_id(), m.getMem_name(), m.getMem_time(), m.getMem_age(), m.getMem_money()
            };
            tableModel.addRow(data);
        }
    }

    public void refreshSeatData() {
        seatPanel.removeAll();
        
        ArrayList<SeatDTO> list = sDao.getAllSeats();
        
        for (SeatDTO s : list) {
            JButton btn = new JButton(s.getSeatIdx() + "번");

            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setMargin(new Insets(0, 0, 0, 0));   

            // 상태가 1(사용중)이면 빨간색, 아니면 초록색
            if (s.getStatus() == 1) {
                int age = s.getMemAge();
                if(age >= 19) {
                    btn.setBackground(new Color(231, 76, 60));
                    btn.setText("<html>"+s.getMemName()+"<br>호갱님</html>");
                } else {
                    btn.setBackground(new Color(243, 156, 18)); 
                    btn.setText("<html><center><b><small>청소년</small></b><br><br>" + s.getMemName() + "</center></html>");
                    btn.setToolTipText(age + "세 - 22시 이후 퇴실 대상"); // 마우스 올리면 안내
                }
                
            } else {
                btn.setBackground(new Color(46, 204, 113));
            }
            seatPanel.add(btn);
        }
        
        seatPanel.revalidate();
        seatPanel.repaint();
    }
    // --------------------------------------------------------
    // 1. 회원 관리 패널 (테이블)
    // --------------------------------------------------------
    public JPanel createMemberPanel() {
        JPanel p = new JPanel(new BorderLayout());
        
        // 컬럼 이름 설정
        String[] cols = {"번호", "아이디", "이름", "잔여시간(분)","나이", "보유머니"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        
        memberTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(memberTable);

        JPanel bottomPanel = new JPanel();
        JButton btnRefresh = new JButton("회원목록 새로고침");
        JButton btnSave = new JButton("수정사항 저장");
        JButton btnPass = new JButton("패스워드 변경");
        JButton btnRemove = new JButton("회원정보 삭제");

        // [버튼 클릭 이벤트] DB에서 회원 가져오기
        btnRefresh.addActionListener(e -> {
            refreshMemberData();
        });
        btnSave.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "행을 선택하세요..");
                return;
            }
            try {
                int idx = (int) tableModel.getValueAt(row, 0);
                String name = tableModel.getValueAt(row, 2).toString(); 
                int time = Integer.parseInt(tableModel.getValueAt(row, 3).toString());
                int age = Integer.parseInt(tableModel.getValueAt(row, 4).toString());
                int money = Integer.parseInt(tableModel.getValueAt(row, 5).toString());
                
                MemberDTO m = new MemberDTO();
                m.setMem_idx(idx); 
                m.setMem_name(name);
                m.setMem_time(time);
                m.setMem_age(age);
                m.setMem_money(money);

                int result = mDao.updateMember(m);

                    if (result > 0) {
                        JOptionPane.showMessageDialog(null, "DB 저장 성공!");
                    } else {
                        JOptionPane.showMessageDialog(null, "저장 실패!");
                    }
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, "데이터 형식이 맞지 않습니다.");
                    e1.printStackTrace();
            }

        });
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
                int idx = (int) tableModel.getValueAt(row, 0);

                MemberDTO m = new MemberDTO();
                m.setMem_idx(idx);
                m.setMem_pass(newPass);
                int result = mDao.updatePass(idx, newPass); 
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "비밀번호가 변경되었습니다.");
                }

            }

        });
        btnRemove.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "행을 선택하세요..");
                return;
            }
            int idx = (int) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 2);

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

        p.add(scroll, BorderLayout.CENTER);
        p.add(bottomPanel, BorderLayout.SOUTH); 
            return p;
    }

    // --------------------------------------------------------
    // 2. 좌석 현황 패널 (그리드 버튼)
    // --------------------------------------------------------
    public JPanel createSeatPanel() {
        JPanel p = new JPanel(new BorderLayout());
        
        seatPanel = new JPanel(new GridLayout(5, 10, 10, 10)); 
        seatPanel.setBackground(new Color(230, 230, 230)); 
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        JButton btnRefresh = new JButton("좌석상태 새로고침");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        btnRefresh.setPreferredSize(new Dimension(0, 50));

        seatPanel = new JPanel(new GridLayout(5, 10, 5, 5));
        // [버튼 클릭 이벤트]
        btnRefresh.addActionListener(e -> {
            refreshSeatData();
        });
        p.add(seatPanel, BorderLayout.CENTER);
        p.add(btnRefresh, BorderLayout.SOUTH);
        return p;
    }

    // --------------------------------------------------------
    // 3. 매출 조회 패널 (검증 로직 복구 + 테이블 적용)
    // --------------------------------------------------------
    public JPanel createRevenuePanel() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] years = {"2024", "2025", "2026"};
        String[] months = new String[12];
        for(int i=0; i < 12; i++) months[i] = String.format("%02d", i+1);

        String[] days = new String[32];
        days[0] = "전체";
        for (int i = 1; i <= 31; i++) days[i] = String.format("%02d", i);

        JComboBox<String> yearCombo = new JComboBox<>(years);
        JComboBox<String> monthCombo = new JComboBox<>(months);
        JComboBox<String> dayCombo = new JComboBox<>(days);

        LocalDate now = LocalDate.now();
        yearCombo.setSelectedItem(String.valueOf(now.getYear()));
        monthCombo.setSelectedItem(String.format("%02d", now.getMonthValue()));
        dayCombo.setSelectedItem("전체"); 

        JButton btnSearch = new JButton("매출 조회");
        btnSearch.setBackground(new Color(70, 130, 180));
        btnSearch.setForeground(Color.WHITE);

        controlPanel.add(yearCombo);
        controlPanel.add(new JLabel("년"));
        controlPanel.add(monthCombo);
        controlPanel.add(new JLabel("월"));
        controlPanel.add(dayCombo);
        controlPanel.add(new JLabel("일"));
        controlPanel.add(btnSearch);

        // [중앙] 테이블
        String[] headers = {"날짜/시간", "회원이름", "구분", "금액"};
        DefaultTableModel logModel = new DefaultTableModel(headers, 0);
        JTable table = new JTable(logModel);
        
        JScrollPane scroll = new JScrollPane(table);
        table.setAutoCreateRowSorter(true);

        // [하단] 합계 라벨
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        revenueLabel = new JLabel("조회된 결과 없음");
        revenueLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        revenueLabel.setForeground(Color.BLACK);
        bottomPanel.add(revenueLabel);

        // -------------------------------------------------------
        // [버튼 클릭 이벤트] 핵심 로직
        // -------------------------------------------------------
        btnSearch.addActionListener(e -> {
            String y = (String) yearCombo.getSelectedItem();
            String m = (String) monthCombo.getSelectedItem();
            String d = (String) dayCombo.getSelectedItem();

            LocalDate today = LocalDate.now();
            try {
                LocalDate selectedDate;
                if(d.equals("전체")) {
                    // '전체'를 선택했으면 그 달의 1일로 설정해서 비교
                    selectedDate = LocalDate.of(Integer.parseInt(y), Integer.parseInt(m), 1);
                    // 만약 선택한 달이 현재 달보다 미래라면? (예: 지금 2월인데 3월 조회)
                    if (selectedDate.isAfter(today.withDayOfMonth(1))) { // 오늘 날짜가 속한 달의 1일과 비교
                         JOptionPane.showMessageDialog(null, "미래의 날짜는 조회할 수 없습니다.");
                         return; // 함수 종료
                    }
                } else {
                    // 구체적인 날짜를 선택한 경우
                    selectedDate = LocalDate.of(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d));
                    if(selectedDate.isAfter(today)) {
                        JOptionPane.showMessageDialog(null, "오늘("+today+") 이후의 데이터는 조회 불가능합니다.");
                        return; // 함수 종료
                    }
                }
            } catch (Exception e1) {
                // 존재하지 않는 날짜 예외 처리
                JOptionPane.showMessageDialog(null, "존재하지 않는 날짜입니다.");
                return; 
            }

            // 2. 검색 조건 문자열 만들기
            String targetDate = "";
            if (d.equals("전체")) {
                targetDate = y + "-" + m;
            } else {
                targetDate = y + "-" + m + "-" + d;
            }

            // 3. DAO 호출
            ArrayList<LogDTO> list = lDao.getLogList(targetDate);
            
            // 4. 테이블 리셋 및 데이터 채우기
            logModel.setRowCount(0);
            int totalSum = 0;
            int foodSum = 0;
            int timeSum = 0;
            for (LogDTO log : list) {
                String typeStr = (log.getLogType() == 1) ? "PC요금" : "음식";

                Object[] rowData = {
                    log.getLogDate(),   
                    log.getMemName(),   
                    typeStr,           
                    String.format("%,d 원", log.getLogAmount()) 
                };
                logModel.addRow(rowData);
                if(typeStr.equals("PC요금")) {
                    timeSum += log.getLogAmount();
                } else {
                    foodSum += log.getLogAmount();
                }
                totalSum += log.getLogAmount();
            }

            // 5. 합계 라벨 갱신
            if (list.size() == 0) {
                revenueLabel.setText("조회된 매출 내역이 없습니다.");
            } else {
                revenueLabel.setText(String.format(
                    "음식 매출: %,d원, PC 요금 매출: %,d원, 총 매출 합계 : %,d 원",  foodSum, timeSum, totalSum));
            }
        });

        p.add(controlPanel, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(bottomPanel, BorderLayout.SOUTH);

        return p;
    }


}