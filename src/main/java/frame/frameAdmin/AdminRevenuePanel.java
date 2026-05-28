package frame.frameAdmin;

import db.LogDAO;
import vo.LogDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
public class AdminRevenuePanel extends JPanel {

    private LogDAO lDao;
    private JLabel revenueLabel; 
    private DefaultTableModel logModel; 

    public AdminRevenuePanel(LogDAO lDao) {
        this.lDao = lDao;
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        ArrayList<String> yearList = new ArrayList<>();

        int startYear = 2023; // 최소(시작) 연도
        int currentYear = LocalDate.now().getYear();
        for (int y = startYear; y <= currentYear; y++) {
            yearList.add(String.valueOf(y));
        }


        String[] months = new String[12];
        for(int i=0; i < 12; i++) months[i] = String.format("%02d", i+1);

        String[] days = new String[32];
        days[0] = "전체";
        for (int i = 1; i <= 31; i++) days[i] = String.format("%02d", i);

        JComboBox<String> yearCombo = new JComboBox<>(yearList.toArray(new String[0]));
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
        logModel = new DefaultTableModel(headers, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class;
                return super.getColumnClass(columnIndex);
            }
        };
        JTable table = new JTable(logModel);
        
        JScrollPane scroll = new JScrollPane(table);
        table.setAutoCreateRowSorter(true);

        // 금액 컬럼 우측 정렬 및 포맷팅
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Number) {
                    setText(String.format("%,d 원", ((Number) value).intValue()));
                    setHorizontalAlignment(JLabel.RIGHT);
                }
                return this;
            }
        });

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
                    log.getLogAmount()
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

        add(controlPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
