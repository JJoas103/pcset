package frame;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameBase extends JFrame{

    private static FrameBase instance;


    private FrameBase() {
        setTitle("PC방 회원관리");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    
    }
    public static FrameBase getInstance(JPanel panel) {
        if(instance == null) {
            instance = new FrameBase();
        }
        instance.getContentPane().removeAll();
        instance.getContentPane().add(panel);
        instance.pack();
        instance.setLocationRelativeTo(null);
        instance.revalidate();
        instance.repaint();
        instance.setVisible(true);
        return instance;
    } 
    public static FrameBase getInstance(JFrame frame) {
        if(instance == null) {
            instance = new FrameBase();
        }
        instance.getContentPane().removeAll();
        instance.getContentPane().add(frame);
        instance.pack();
        instance.setLocationRelativeTo(null); 
        instance.revalidate();
        instance.repaint();
        instance.setVisible(true);
        return instance;
        
    } 
    public static FrameBase getInstance(JDialog dialog) {
        if(instance == null) {
            instance = new FrameBase();
        }
        instance.getContentPane().removeAll();
        instance.getContentPane().add(dialog);
        instance.pack(); 
        instance.setLocationRelativeTo(null); 
        instance.revalidate();
        instance.repaint(); 
        instance.setVisible(true);
        return instance;
        
    } 
}

