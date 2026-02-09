package frame.frameAdmin;

import db.FoodDAO;
import vo.FoodDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class AdminFoodPanel extends JPanel {

    private FoodDAO foodDao;
    private JTable foodTable;
    private DefaultTableModel foodModel;
    private JTextField tfFoodIdx, tfFoodName, tfFoodPrice, tfFoodStock;

    public AdminFoodPanel(FoodDAO foodDao) {
        this.foodDao = foodDao;
        setLayout(new BorderLayout());

        String[] headers = {"번호(idx)", "음식명", "가격", "재고"};
        foodModel = new DefaultTableModel(headers, 0);
        foodTable = new JTable(foodModel);
        add(new JScrollPane(foodTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        JPanel inputPanel = new JPanel();
        
        tfFoodIdx = new JTextField(3); 
        tfFoodName = new JTextField(8);
        tfFoodPrice = new JTextField(5);
        tfFoodStock = new JTextField(5);

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
        add(bottomPanel, BorderLayout.SOUTH);

        // 초기 데이터 로드
        refreshFoodTable();


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

                foodDao.insertFood(f);       
                refreshFoodTable();      
                clearFoodInputs();       
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
                foodDao.updateFood(f);
                refreshFoodTable(); clearFoodInputs();
                JOptionPane.showMessageDialog(null, "수정 완료");
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "수정할 음식을 선택하세요"); }
        });

        // 삭제 버튼
        btnDel.addActionListener(e -> {
            try {
                FoodDTO f = new FoodDTO();
                f.setFood_idx(Integer.parseInt(tfFoodIdx.getText()));
                foodDao.deletefood(f);
                refreshFoodTable(); clearFoodInputs();
                JOptionPane.showMessageDialog(null, "삭제 완료");
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "삭제할 음식을 선택하세요"); }
        });

        btnRefresh.addActionListener(e -> refreshFoodTable());
    }

    public void refreshFoodTable() {
        foodModel.setRowCount(0);
        List<FoodDTO> list = foodDao.showFood();
        for (FoodDTO f : list) {
            Vector<Object> row = new Vector<>();
            row.add(f.getFood_idx());
            row.add(f.getFood_name());
            row.add(f.getFood_price());
            row.add(f.getFood_stock());
            foodModel.addRow(row);
        }
    }

    private void clearFoodInputs() {
        tfFoodIdx.setText(""); tfFoodName.setText("");
        tfFoodPrice.setText(""); tfFoodStock.setText("");
    }
}
