package frame.frameAdmin; // 패키지명 확인

import db.AdminDAO;
import vo.FoodDTO;
import vo.OrdersDTO;
import vo.TimeDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class AdminTestGUI extends JDialog{

    AdminDAO dao = new AdminDAO();
    JTabbedPane tabbedPane;

    // 변수 선언
    JTable foodTable, orderTable, timeTable;
    DefaultTableModel foodModel, orderModel, timeModel;
    JTextField tfFoodName, tfFoodPrice, tfFoodStock, tfFoodIdx;
    JTextField tfTimeHour, tfTimePrice;

    public AdminTestGUI() {
        setTitle("PC방 관리자 기능");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("1. 음식 관리", createFoodPanel(dao));
        tabbedPane.addTab("2. 주문 관리", createOrderPanel(dao));
        tabbedPane.addTab("3. 시간/요금 관리", createTimePanel(dao));

        add(tabbedPane);

        // 초기 데이터 로드
        refreshFoodTable();
        refreshOrderTable();
        refreshTimeTable();
        setVisible(true);
    }

    // 1. 음식 관리 패널
    public static JPanel createFoodPanel(AdminDAO adminDao) {
        JPanel panel = new JPanel(new BorderLayout());

        String[] headers = {"번호(idx)", "음식명", "가격", "재고"};
        DefaultTableModel foodModel = new DefaultTableModel(headers, 0);
        JTable foodTable = new JTable(foodModel);
        panel.add(new JScrollPane(foodTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        JPanel inputPanel = new JPanel();
        
        JTextField tfFoodIdx = new JTextField(3); 
        JTextField tfFoodName = new JTextField(8);
        JTextField tfFoodPrice = new JTextField(5);
        JTextField tfFoodStock = new JTextField(5);

        inputPanel.add(new JLabel("번호:")); inputPanel.add(tfFoodIdx);
        inputPanel.add(new JLabel("이름:")); inputPanel.add(tfFoodName);
        inputPanel.add(new JLabel("가격:")); inputPanel.add(tfFoodPrice);
        inputPanel.add(new JLabel("재고:")); inputPanel.add(tfFoodStock);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("추가");
        JButton btnUpdate = new JButton("수정");
        JButton btnDel = new JButton("삭제");
        JButton btnRefresh = new JButton("새로고침");

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDel); btnPanel.add(btnRefresh);
        bottomPanel.add(inputPanel); bottomPanel.add(btnPanel);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // 로컬 refreshFoodTable 메소드
        Runnable refreshFoodTable = () -> {
            foodModel.setRowCount(0);
            List<FoodDTO> list = adminDao.showFood();
            for (FoodDTO f : list) {
                Vector<Object> row = new Vector<>();
                row.add(f.getFood_idx());
                row.add(f.getFood_name());
                row.add(f.getFood_price());
                row.add(f.getFood_stock());
                foodModel.addRow(row);
            }
        };

        // 로컬 clearFoodInputs 메소드
        Runnable clearFoodInputs = () -> {
            tfFoodIdx.setText(""); tfFoodName.setText("");
            tfFoodPrice.setText(""); tfFoodStock.setText("");
        };

        // 초기 데이터 로드
        refreshFoodTable.run();


        // 테이블 클릭 시 값 채우기
        foodTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = foodTable.getSelectedRow();
                tfFoodIdx.setText(foodModel.getValueAt(row, 0).toString());
                tfFoodName.setText(foodModel.getValueAt(row, 1).toString());
                tfFoodPrice.setText(foodModel.getValueAt(row, 2).toString());
                tfFoodStock.setText(foodModel.getValueAt(row, 3).toString());
            }
        });

        // ★★★ [수정] 추가 버튼 (번호도 같이 보냄) ★★★
        btnAdd.addActionListener(e -> {
            try {
                FoodDTO f = new FoodDTO();
                // 사용자가 입력한 번호를 가져옴
                f.setFood_idx(Integer.parseInt(tfFoodIdx.getText())); 
                f.setFood_name(tfFoodName.getText());
                f.setFood_price(Integer.parseInt(tfFoodPrice.getText()));
                f.setFood_stock(Integer.parseInt(tfFoodStock.getText()));

                adminDao.insertFood(f);       
                refreshFoodTable.run();      
                clearFoodInputs.run();       
                JOptionPane.showMessageDialog(null, "처리 완료!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "번호, 가격, 재고는 숫자만 입력하세요!");
            } catch (Exception ex) { 
                ex.printStackTrace(); 
                JOptionPane.showMessageDialog(null, "에러 발생 (중복된 번호 등 확인)"); 
            }
        });

        // 수정 버튼
        btnUpdate.addActionListener(e -> {
            try {
                FoodDTO f = new FoodDTO();
                f.setFood_idx(Integer.parseInt(tfFoodIdx.getText()));
                f.setFood_name(tfFoodName.getText());
                f.setFood_price(Integer.parseInt(tfFoodPrice.getText()));
                f.setFood_stock(Integer.parseInt(tfFoodStock.getText()));
                adminDao.updateFood(f);
                refreshFoodTable.run(); clearFoodInputs.run();
                JOptionPane.showMessageDialog(null, "수정 완료");
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "수정할 음식을 선택하세요"); }
        });

        // 삭제 버튼
        btnDel.addActionListener(e -> {
            try {
                FoodDTO f = new FoodDTO();
                f.setFood_idx(Integer.parseInt(tfFoodIdx.getText()));
                adminDao.deletefood(f);
                refreshFoodTable.run(); clearFoodInputs.run();
                JOptionPane.showMessageDialog(null, "삭제 완료");
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "삭제할 음식을 선택하세요"); }
        });

        btnRefresh.addActionListener(e -> refreshFoodTable.run());
        return panel;
    }

    // 2. 주문 관리 패널
    public static JPanel createOrderPanel(AdminDAO adminDao) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] headers = {"주문번호", "회원명", "음식명", "남은재고", "좌석번호"};
        DefaultTableModel orderModel = new DefaultTableModel(headers, 0);
        JTable orderTable = new JTable(orderModel);
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnDel = new JButton("주문 삭제 (조리완료)");
        JButton btnRefresh = new JButton("새로고침");
        btnPanel.add(btnDel); btnPanel.add(btnRefresh);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // 로컬 refreshOrderTable 메소드
        Runnable refreshOrderTable = () -> {
            orderModel.setRowCount(0);
            List<OrdersDTO> list = adminDao.showOrder();
            for (OrdersDTO o : list) {
                Vector<Object> row = new Vector<>();
                row.add(o.getOd_idx());
                row.add(o.getMem_name());
                row.add(o.getFood_name());
                row.add(o.getFood_stock());
                row.add(o.getSeat_idx());
                orderModel.addRow(row);
            }
        };

        // 초기 데이터 로드
        refreshOrderTable.run();

        btnDel.addActionListener(e -> {
            int row = orderTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(null, "선택해주세요"); return; }
            int odIdx = Integer.parseInt(orderModel.getValueAt(row, 0).toString());
            adminDao.deleteOrder(odIdx);
            refreshOrderTable.run();
            JOptionPane.showMessageDialog(null, "완료!");
        });
        btnRefresh.addActionListener(e -> refreshOrderTable.run());
        return panel;
    }

    // 3. 시간 관리 패널
    public static JPanel createTimePanel(AdminDAO adminDao) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] headers = {"시간", "가격"};
        DefaultTableModel timeModel = new DefaultTableModel(headers, 0);
        JTable timeTable = new JTable(timeModel);
        panel.add(new JScrollPane(timeTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new GridLayout(2, 1));
        JPanel inp = new JPanel();
        JTextField tfTimeHour = new JTextField(5); JTextField tfTimePrice = new JTextField(5);
        inp.add(new JLabel("시간:")); inp.add(tfTimeHour);
        inp.add(new JLabel("가격:")); inp.add(tfTimePrice);

        JPanel btns = new JPanel();
        JButton btnAdd = new JButton("추가"); JButton btnUpd = new JButton("수정");
        JButton btnDel = new JButton("삭제"); JButton btnRef = new JButton("새로고침");
        btns.add(btnAdd); btns.add(btnUpd); btns.add(btnDel); btns.add(btnRef);
        
        bot.add(inp); bot.add(btns);
        panel.add(bot, BorderLayout.SOUTH);

        // 로컬 refreshTimeTable 메소드
        Runnable refreshTimeTable = () -> {
            timeModel.setRowCount(0);
            List<TimeDTO> list = adminDao.getAllTime();
            for (TimeDTO t : list) {
                Vector<Object> row = new Vector<>();
                row.add(t.getHour());
                row.add(t.getPrice());
                timeModel.addRow(row);
            }
        };

        // 초기 데이터 로드
        refreshTimeTable.run();

        timeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = timeTable.getSelectedRow();
                tfTimeHour.setText(timeModel.getValueAt(row, 0).toString());
                tfTimePrice.setText(timeModel.getValueAt(row, 1).toString());
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                adminDao.inserthour(Integer.parseInt(tfTimeHour.getText()), Integer.parseInt(tfTimePrice.getText()));
                refreshTimeTable.run(); tfTimeHour.setText(""); tfTimePrice.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "추가 실패: " + ex.getMessage()); }
        });
        btnUpd.addActionListener(e -> {
            try {
                adminDao.updateTimePrice(Integer.parseInt(tfTimeHour.getText()), Integer.parseInt(tfTimePrice.getText()));
                refreshTimeTable.run();
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "수정 실패: " + ex.getMessage()); }
        });
        btnDel.addActionListener(e -> {
            try {
                adminDao.deleteTime(Integer.parseInt(tfTimeHour.getText()));
                refreshTimeTable.run(); tfTimeHour.setText(""); tfTimePrice.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "삭제 실패: " + ex.getMessage()); }
        });
        btnRef.addActionListener(e -> refreshTimeTable.run());
        return panel;
    }

    // 새로고침 메서드들
    public void refreshFoodTable() {
        foodModel.setRowCount(0);
        List<FoodDTO> list = dao.showFood();
        for (FoodDTO f : list) {
            Vector<Object> row = new Vector<>();
            row.add(f.getFood_idx());
            row.add(f.getFood_name());
            row.add(f.getFood_price());
            row.add(f.getFood_stock());
            foodModel.addRow(row);
        }
    }
    public void refreshOrderTable() {
        orderModel.setRowCount(0);
        List<OrdersDTO> list = dao.showOrder();
        for (OrdersDTO o : list) {
            Vector<Object> row = new Vector<>();
            row.add(o.getOd_idx());
            row.add(o.getMem_name());
            row.add(o.getFood_name());
            row.add(o.getFood_stock());
            row.add(o.getSeat_idx());
            orderModel.addRow(row);
        }
    }
    public void refreshTimeTable() {
        timeModel.setRowCount(0);
        List<TimeDTO> list = dao.getAllTime();
        for (TimeDTO t : list) {
            Vector<Object> row = new Vector<>();
            row.add(t.getHour());
            row.add(t.getPrice());
            timeModel.addRow(row);
        }
    }
    public void clearFoodInputs() {
        tfFoodIdx.setText(""); tfFoodName.setText("");
        tfFoodPrice.setText(""); tfFoodStock.setText("");
    }

    
}
