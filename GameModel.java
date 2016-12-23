package FreeCell;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;

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

    public boolean move(CardCascade from, CardCascade onto){
        if(from.isEmpty()) return false;
        Card c = from.peek();
        if (!onto.canAdd(c)) return false;
        try{
            from.pop();
            onto.push(c);
            notifyEveryone();
            moveHistory.push(new Move(from,onto));
            return true;
        }
        catch(NonRemovalCardException e){
            System.err.println(e.getMessage());
            return false;
        }
    }
}
