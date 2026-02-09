package frame.frameAdmin;

import db.TimeDAO;
import vo.TimeDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class AdminTimePanel extends JPanel {

    private TimeDAO timeDao;
    private JTable timeTable;
    private DefaultTableModel timeModel;
    private JTextField tfTimeHour, tfTimePrice;

    public AdminTimePanel(TimeDAO timeDao) {
        this.timeDao = timeDao;
        setLayout(new BorderLayout());

        String[] headers = {"시간", "가격"};
        timeModel = new DefaultTableModel(headers, 0);
        timeTable = new JTable(timeModel);
        add(new JScrollPane(timeTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new GridLayout(2, 1));
        JPanel inp = new JPanel();
        tfTimeHour = new JTextField(5);
        tfTimePrice = new JTextField(5);
        inp.add(new JLabel("시간:")); inp.add(tfTimeHour);
        inp.add(new JLabel("가격:")); inp.add(tfTimePrice);

        JPanel btns = new JPanel();
        JButton btnAdd = new JButton("추가"); JButton btnUpd = new JButton("수정");
        JButton btnDel = new JButton("삭제"); JButton btnRef = new JButton("새로고침");
        btns.add(btnAdd); btns.add(btnUpd); btns.add(btnDel); btns.add(btnRef);
        
        bot.add(inp); bot.add(btns);
        add(bot, BorderLayout.SOUTH);

        // 초기 데이터 로드
        refreshTimeTable();

        timeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = timeTable.getSelectedRow();
                tfTimeHour.setText(timeModel.getValueAt(row, 0).toString());
                tfTimePrice.setText(timeModel.getValueAt(row, 1).toString());
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                timeDao.inserthour(Integer.parseInt(tfTimeHour.getText()), Integer.parseInt(tfTimePrice.getText()));
                refreshTimeTable(); clearTimeInputs();
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "추가 실패: " + ex.getMessage()); }
        });
        btnUpd.addActionListener(e -> {
            try {
                timeDao.updateTimePrice(Integer.parseInt(tfTimeHour.getText()), Integer.parseInt(tfTimePrice.getText()));
                refreshTimeTable();
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "수정 실패: " + ex.getMessage()); }
        });
        btnDel.addActionListener(e -> {
            try {
                timeDao.deleteTime(Integer.parseInt(tfTimeHour.getText()));
                refreshTimeTable(); clearTimeInputs();
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "삭제 실패: " + ex.getMessage()); }
        });
        btnRef.addActionListener(e -> refreshTimeTable());
    }

    public void refreshTimeTable() {
        timeModel.setRowCount(0);
        List<TimeDTO> list = timeDao.getAllTime();
        for (TimeDTO t : list) {
            Vector<Object> row = new Vector<>();
            row.add(t.getHour());
            row.add(t.getPrice());
            timeModel.addRow(row);
        }
    }

    private void clearTimeInputs() {
        tfTimeHour.setText("");
        tfTimePrice.setText("");
    }
}
