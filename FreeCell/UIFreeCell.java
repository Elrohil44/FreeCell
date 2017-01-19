package FreeCell;

import FreeCell.Exceptions.EndOfHistoryException;
import FreeCell.Exceptions.WonGameException;
import com.sun.jmx.remote.security.JMXPluggableAuthenticator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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

        JCheckBox AutoEnd = new JCheckBox("Auto Ending");
        AutoEnd.addItemListener(new AutoEndListener());

        JButton TipButton = new JButton("Get tip");
        TipButton.addActionListener(new GetTip());

        JButton GameBtn = new JButton("New Game");
        GameBtn.addActionListener(new ActionNewGame());

        JButton Undo = new JButton("Undo");
        Undo.addActionListener(new ActionUndo());

        JButton TryAgainButton = new JButton("Try again the same");
        TryAgainButton.addActionListener(new TryAgain());

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(TryAgainButton);
        controlPanel.add(TipButton);
        controlPanel.add(GameBtn);
        controlPanel.add(Undo);
        controlPanel.add(AutoEnd);

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

    class ActionUndo implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            try{
                model.undo();
            }catch(EndOfHistoryException exc){
                Notification note = new Notification(exc.getMessage());
            }
        }
    }

    class AutoEndListener implements ItemListener{
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange()==1) model.setAutoending(true);
            else model.setAutoending(false);
        }
    }

    class GetTip implements  ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            model.getTip();
        }
    }

    class TryAgain implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                model.undoAll();
            } catch (EndOfHistoryException exc){
                Notification note = new Notification(exc.getMessage());
            }
        }
    }
}
