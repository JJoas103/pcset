package frame.frameAdmin;

import db.LogDAO;
import db.MemberDAO;
import db.OrderDAO;
import db.SeatDAO;
import db.TimeDAO;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import db.FoodDAO;

public class AdminView extends JDialog {
    
    
    MemberDAO mDao = new MemberDAO();
    SeatDAO sDao = new SeatDAO();
    LogDAO lDao = new LogDAO();
    FoodDAO fDao = new FoodDAO();
    TimeDAO tDao = new TimeDAO();
    OrderDAO oDao = new OrderDAO();

    JTabbedPane tabPane;

    public AdminView() {
        setTitle("PC방 관리자 모드");
        setSize(1200, 800);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null); 
        
        tabPane = new JTabbedPane();
        tabPane.addTab("1. 회원 관리", new AdminMemberPanel(mDao));
        tabPane.addTab("2. 좌석 현황", new AdminSeatPanel(sDao, mDao));
        tabPane.addTab("3. 매출 조회", new AdminRevenuePanel(lDao));
        tabPane.addTab("4. 음식 관리", new AdminFoodPanel(fDao)); 
        tabPane.addTab("5. 주문 관리", new AdminOrderPanel(oDao));
        tabPane.addTab("6. 시간/요금 관리", new AdminTimePanel(tDao));

        add(tabPane);
        setVisible(true);
           
    }
}