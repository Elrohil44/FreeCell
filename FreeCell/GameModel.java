package FreeCell;

import FreeCell.Cascades.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import FreeCell.Card.*;
import FreeCell.Exceptions.*;

/**
 * Created by Wiesiek on 2016-12-22.
 */
public class GameModel{
    private CardCell[] cardCells;
    private CardTableau[] cardTableaus;
    private CardFoundation[] cardFoundations;

    private ArrayList<ChangeListener> changeListeners;
    private ArrayDeque<Move> moveHistory = new ArrayDeque<Move>();
    private ArrayList<CardCascade> cascades;
    private boolean autoending=false;
    private JFrame frame;
    private Move movePossible;
    private boolean waiting;


    public GameModel(){
        cascades = new ArrayList<CardCascade>();

        cardCells = new CardCell[4];
        cardFoundations = new CardFoundation[4];
        cardTableaus = new CardTableau[8];

        for(int i=0;i<4;i++){
            cardCells[i] = new CardCell();
            cardFoundations[i] = new CardFoundation();
            cardTableaus[2*i] = new CardTableau();
            cardTableaus[2*i+1] = new CardTableau();
        }

        Collections.addAll(cascades,cardFoundations);
        Collections.addAll(cascades,cardCells);
        Collections.addAll(cascades,cardTableaus);
        changeListeners = new ArrayList<ChangeListener>();

        reset();
    }

    public void reset(){
        Deck deck = new Deck();
        for (CardCascade c: cascades){
            c.reset();
        }
        int cascadeNr=0;
        for (Card c: deck.iterate()){
            cardTableaus[cascadeNr++].forcePush(c);
            cascadeNr%=cardTableaus.length;
        }
        notifyEveryone();
    }

    public boolean isWaiting() {
        return waiting;
    }

    public Iterable<CardCascade> iterate(){
        return cascades;
    }

    public CardTableau getCardTableau(int index) {
        return cardTableaus[index];
    }

    public CardTableau[] getCardTableaus() {
        return cardTableaus;
    }

    public CardCell getCardCell(int index) {
        return cardCells[index];
    }

    public CardCell[] getCardCells() {
        return cardCells;
    }

    public CardFoundation getCardFoundation(int index) {
        return cardFoundations[index];
    }

    public CardFoundation[] getCardFoundations() {
        return cardFoundations;
    }

    public void addChangeListener(ChangeListener toBeNotified){
        changeListeners.add(toBeNotified);
    }

    public void notifyEveryone(){
        for (ChangeListener c: changeListeners){
            c.stateChanged(new ChangeEvent("Game has been changed"));
        }
    }

    public int getMinAtFoundations(){
        int min=999;
        for (CardFoundation f: cardFoundations){
            if(f.isEmpty()) return 0;
            if (!f.isEmpty() && f.peek().getFace().ordinal()<min){
                min=f.peek().getFace().ordinal();

            }
        }
        if (min==999) return 0;

        return min;
    }

    public int findFoundationSuit(CardSuit suit){
        int empty=-1;
        for (int i=3;i>=0;i--){
            if (cardFoundations[i].getSuit()==suit) return i;
            else if (cardFoundations[i].isEmpty()) empty=i;
        }
        return empty;
    }

    public int getEmptyCell(){
        for(int i=0;i<4;i++){
            if (cardCells[i].isEmpty()) return i;
        }
        return 0;
    }

    public void moveToFoundation(CardCascade from) throws WrongMoveException, WonGameException, EndGameException {
        if(from.isEmpty()) return;
        try{
            move(from, cardFoundations[findFoundationSuit(from.peek().getSuit())],false);
        }
        catch (WrongMoveException|EndGameException|WonGameException e){
            throw (e);
        }
    }

    public boolean moveToCell(CardCascade from) throws WrongMoveException, WonGameException, EndGameException {
        try{
            return move(from,cardCells[getEmptyCell()],false);
        } catch (WrongMoveException|EndGameException|WonGameException e){
            throw (e);
        }
    }

    public boolean move(CardCascade from, CardCascade onto,boolean multi) throws WrongMoveException,EndGameException,WonGameException{
        if(from.isEmpty()) return false;
        if(!from.canRemove()) return false;
        if(onto.isEmpty() && onto.getType()!=CascadeType.FOUNDATION && onto.getType()!=CascadeType.CELL){
            int count = Math.min(getMovesAvailable(onto),from.countInOrder());
            if(count>1){
                waiting=true;
                frame = new DecisionFrame(from,onto,count);

            } else{
                moveCards(from,onto,count);
            }

        }
        else if(onto.isEmpty()){
            if(!onto.canAdd(from.peek())){
                throw (new WrongMoveException("Nie możesz położyć tej karty na tym miejscu"));
            }
            moveCards(from,onto,1);

        }
        else if(!multi || onto.getType()==CascadeType.FOUNDATION || onto.getType()==CascadeType.CELL) {
            if (onto.canAdd(from.peek())) {
                moveCards(from, onto, 1);
            } else {
                if(onto.getType()==CascadeType.FOUNDATION) {
                    throw (new WrongMoveException("Nie możesz położyć tej karty na tym miejscu"));
                } else if (onto.getType()==CascadeType.CELL){
                    throw (new WrongMoveException("To miejsce jest zajęte"));
                } else {
                    throw (new WrongMoveException("Nieprawidłowy ruch"));
                }
            }
        } else {
            int count = onto.peek().getFace().ordinal() - from.peek().getFace().ordinal();
            if (onto.peek().getFace().ordinal() <= from.peek().getFace().ordinal())
                throw (new WrongMoveException("Nieprawidłowy ruch"));
            if (count % 2 == 0 && onto.peek().getSuit().getColor() != from.peek().getSuit().getColor())
                throw (new WrongMoveException("Nieprawidłowy ruch"));
            if (count % 2 == 1 && onto.peek().getSuit().getColor() == from.peek().getSuit().getColor())
                throw (new WrongMoveException("Nieprawidłowy ruch"));
            if (from.countInOrder() < count) throw (new WrongMoveException("Nieprawidłowy ruch"));
            if (getMovesAvailable(onto) < count)
                throw (new WrongMoveException("Próbujesz przenieść " + count + " kart, a możesz tylko " + getMovesAvailable(onto)));
            moveCards(from, onto, count);

        }

        return true;
    }

    public LinkedList<CardCascade> getRemovable(){
        LinkedList<CardCascade> cascades = new LinkedList<>();
        for(CardCell cell: cardCells) {
            cascades.add(cell);
        }
        for(CardTableau tableau: cardTableaus){
            cascades.add(tableau);
        }
        return cascades;
    }

    public void checkStatus() throws EndGameException,WonGameException{
        boolean allEmpty=true;
        boolean moveAvailable=false;
        LinkedList<CardCascade> cascades = getRemovable();
        for(CardCascade cascade: cascades){
            if(!cascade.isEmpty()){
                allEmpty=false;
                break;
            }
        }

        if(allEmpty) throw (new WonGameException("Congratulations! You won!"));
        for(CardCascade from: cascades){
            for(CardCascade onto: iterate()){

                if(from!=onto && !from.isEmpty() && onto.canAdd(from.peek())){
                    moveAvailable=true;
                    movePossible=new Move(from,onto,1);
                    break;
                }

            }
            if (moveAvailable) break;
        }

        if(!moveAvailable)
        {
            movePossible=null;
            throw (new EndGameException("Brak możliwych ruchów"));
        }


    }


    public void getTip(){
        try {
            checkStatus();
            if(movePossible!=null) {
                movePossible.getFrom().setHighlighted(true);
                movePossible.getOnto().setHighlighted(true);
                notifyEveryone();
            }
        } catch(WonGameException|EndGameException e){
            Notification note = new Notification(e.getMessage());
        }
    }

    public void autoEnding(){
        boolean flag=true;
        LinkedList<CardCascade> cascades = getRemovable();
        while(flag)
        {
            flag=false;
            try {
                for (CardCascade cell : cascades) {
                    if (!cell.isEmpty()) {
                        if (cardFoundations[findFoundationSuit(cell.peek().getSuit())].canAdd(cell.peek()) && cell.peek().getFace().ordinal() - 1 <= getMinAtFoundations()) {
                                Card card = cell.pop();
                                cardFoundations[findFoundationSuit(card.getSuit())].push(card);
                                moveHistory.push(new Move(cell, cardFoundations[findFoundationSuit(card.getSuit())], 1));
                            flag = true;
                            notifyEveryone();
                        }
                    }
                }
            } catch(NonRemovalCardException e){
                System.err.println(e.getMessage());
            }

        }
    }


    public boolean undo() throws EndOfHistoryException {
        notifyEveryone();
        if(!moveHistory.isEmpty()) {
            Move m = moveHistory.pop();
            CardCascade cascade = new CardCascade();
            for(int i=0;i<m.getCount();i++){
                cascade.push(m.getOnto().forcePop());
            }
            for (Card c: cascade.reverseIterate()) {
                c.setHighlighted(false);
                m.getFrom().forcePush(c);
                notifyEveryone();
            }
        } else throw (new EndOfHistoryException("Koniec historii ruchów"));
        return true;
    }

    public void setAutoending(boolean autoending) {
        this.autoending = autoending;
    }

    public int getEmptyTableausCount(){
        int count=0;
        for (CardTableau tableau: cardTableaus){
            if (tableau.isEmpty()) count++;
        }

        return count;
    }

    public int getEmptyCellsCount(){
        int count=0;
        for (CardCell cell: cardCells){
            if (cell.isEmpty()) count++;
        }

        return count;
    }

    public void moveCards(CardCascade from, CardCascade onto,int count) throws EndGameException,WonGameException {
            CardCascade cascade = new CardCascade();
            for (int i = 0; i < count; i++) {
                try {
                    cascade.push(from.pop());
                } catch (NonRemovalCardException exc) {
                    System.err.println(exc.getMessage());
                }
            }
            for (Card c : cascade.reverseIterate()) {
                onto.push(c);
                notifyEveryone();
            }
            moveHistory.push(new Move(from, onto, count));
            if(autoending) autoEnding();
            try {
                checkStatus();
            } catch (EndGameException | WonGameException e) {
                throw (e);
            }
    }


    public int getMovesAvailable(CardCascade onto){
        if (onto.getType()==CascadeType.CELL && onto.isEmpty()) return 1;
        if (onto.getType()==CascadeType.FOUNDATION) return 1;
        if (onto.getType()==CascadeType.TABLEAU && onto.isEmpty()) return (getEmptyCellsCount()+1)*(getEmptyTableausCount());
        return (getEmptyCellsCount()+1)*(getEmptyTableausCount()+1);
    }

    class MoveCards implements ActionListener{
        CardCascade from;
        CardCascade onto;
        int count;

        public MoveCards(CardCascade from, CardCascade onto,int count){
            this.from=from;
            this.onto=onto;
            this.count=count;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                waiting=false;
                moveCards(from, onto, count);
            } catch(WonGameException|EndGameException exc)
            {
                Notification note = new Notification(exc.getMessage());
            }
            frame.dispose();
        }
    }

    class DecisionFrame extends JFrame{
        public  DecisionFrame(CardCascade from, CardCascade onto,int count){
            super();
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            JButton all = new JButton("Przenieś wszystkie "+count+" karty");
            all.addActionListener(new MoveCards(from, onto, count));
            JButton top = new JButton("Tylko pierwszą");
            top.addActionListener(new MoveCards(from, onto, 1));
            JPanel panel = new JPanel(new FlowLayout());
            panel.add(all);
            panel.add(top);
            setContentPane(panel);
            setResizable(false);
            setLocationRelativeTo(null);
            pack();
            setVisible(true);
            setAlwaysOnTop(true);
        }
    }

}
