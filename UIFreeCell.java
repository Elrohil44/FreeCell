package FreeCell;

import com.sun.jmx.remote.security.JMXPluggableAuthenticator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Wiesiek on 2016-12-23.
 */
public class UIFreeCell extends JFrame {
    private GameModel model = new GameModel();
    private UICard boardDisplay;

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UIFreeCell();
            }
        });
    }

    public UIFreeCell(){
        boardDisplay = new UICard(model);

        JButton GameBtn = new JButton("New Game");
        GameBtn.addActionListener(new ActionNewGame());

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(GameBtn);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(controlPanel,BorderLayout.NORTH);
        content.add(boardDisplay,BorderLayout.CENTER);

        setContentPane(content);
        setTitle("FreeCell");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    class ActionNewGame implements ActionListener{
        public void actionPerformed(ActionEvent e){
            model.reset();
        }
    }
}
