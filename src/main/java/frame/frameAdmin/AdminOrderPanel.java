package frame.frameAdmin;

import db.OrderDAO;
import vo.OrdersDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class AdminOrderPanel extends JPanel {

    private OrderDAO orderDao;
    private JTable orderTable;
    private DefaultTableModel orderModel;

    public AdminOrderPanel(OrderDAO orderDao) {
        this.orderDao = orderDao;
        setLayout(new BorderLayout());

        String[] headers = {"주문번호", "회원명", "음식명", "남은재고", "좌석번호","수량"};
        orderModel = new DefaultTableModel(headers, 0);
        orderTable = new JTable(orderModel);
        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnDel = new JButton("주문 삭제 (조리완료)");
        JButton btnRefresh = new JButton("새로고침");
        btnPanel.add(btnDel); btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        // 초기 데이터 로드
        refreshOrderTable();

        btnDel.addActionListener(e -> {
            int row = orderTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(null, "선택해주세요"); return; }
            int odIdx = Integer.parseInt(orderModel.getValueAt(row, 0).toString());
            orderDao.deleteOrder(odIdx);
            refreshOrderTable();
            JOptionPane.showMessageDialog(null, "완료!");
        });
        btnRefresh.addActionListener(e -> refreshOrderTable());
    }

    public void refreshOrderTable() {
        orderModel.setRowCount(0);
        List<OrdersDTO> list = orderDao.showOrder();
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
}
