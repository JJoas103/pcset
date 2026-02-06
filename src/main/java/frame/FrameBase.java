package frame;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameBase extends JFrame{

    private static FrameBase instance;


    private FrameBase() {//메인 프레임
        setTitle("PC방 회원관리");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
    
    }
    public static FrameBase getInstance(JPanel panel) {
        if(instance == null) {
            instance = new FrameBase();
        }
        instance.getContentPane().removeAll();
        instance.getContentPane().add(panel);
        instance.pack(); // 패널의 선호 크기에 맞춰 프레임 크기 조정
        instance.setLocationRelativeTo(null); // 프레임을 화면 중앙에 배치
        instance.revalidate();
        instance.repaint(); // revalidate 후 repaint 호출이 더 안정적일 수 있음
        instance.setVisible(true);
        return instance;
    } 
    public static FrameBase getInstance(JFrame frame) {
        if(instance == null) {
            instance = new FrameBase();
        }
        instance.getContentPane().removeAll();
        instance.getContentPane().add(frame);
        instance.pack(); // 패널의 선호 크기에 맞춰 프레임 크기 조정
        instance.setLocationRelativeTo(null); // 프레임을 화면 중앙에 배치
        instance.revalidate();
        instance.repaint(); // revalidate 후 repaint 호출이 더 안정적일 수 있음
        instance.setVisible(true);
        return instance;
        
    } 
    public static FrameBase getInstance(JDialog dialog) {
        if(instance == null) {
            instance = new FrameBase();
        }
        instance.getContentPane().removeAll();
        instance.getContentPane().add(dialog);
        instance.pack(); // 패널의 선호 크기에 맞춰 프레임 크기 조정
        instance.setLocationRelativeTo(null); // 프레임을 화면 중앙에 배치
        instance.revalidate();
        instance.repaint(); // revalidate 후 repaint 호출이 더 안정적일 수 있음
        instance.setVisible(true);
        return instance;
        
    } 
}

