package FreeCell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Created by Wiesiek on 2017-01-19.
 */
public class Notification extends JFrame {
    private static boolean isTranslucencySupported;

    public Notification(String s){
        super();
        setLayout(new BorderLayout());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),0.5*getWidth(),0.5*getHeight()));
            }
        });
        JPanel A = new JPanel();
        A.setLayout(new FlowLayout());
        JPanel B = new JPanel();
        B.setLayout(new FlowLayout());
        JPanel C = new JPanel();
        C.setLayout(new GridLayout(2,1));
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        A.add(close);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        B.add(new JLabel(s));
        C.add(A);
        C.add(B);
        JPanel control = new JPanel();
        control.setLayout(new FlowLayout());
        control.add(C);
        add(control);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        isTranslucencySupported = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
        if(isTranslucencySupported){
            setOpacity(0.9f);
        }
        pack();
        setVisible(true);
        setAlwaysOnTop(true);
    }
}
