package FreeCell.Cascades;

import java.util.*;
import FreeCell.Card.*;
import FreeCell.Exceptions.*;
/**
 * Created by Wiesiek on 2016-12-22.
 */
public class CardCascade {
    private ArrayList<Card> cards = new ArrayList<Card>();
    private boolean highlighted=false;

    public void forcePush(Card card){
        cards.add(card);
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public Card forcePop(){
        if (cards.isEmpty()) return null;
        Card c = cards.get(cards.size()-1);
        cards.remove(cards.size()-1);
        return c;
    }

    public boolean push (Card card){
        if (canAdd(card)) {
            cards.add(card);
            return true;
        }
        else
            return false;
    }

    public boolean canAdd(Card card){
        return true;
    }

    public boolean canRemove(){
        return true;
    }

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public Iterable<Card> iterate(){
        return cards;
    }

    public Iterable<Card> reverseIterate(){
        ArrayList<Card> list = new ArrayList<Card>(cards);
        Collections.reverse(list);
        return list;
    }

    public Card pop() throws NonRemovalCardException{
        if(canRemove()){
            return forcePop();
        }
        throw new NonRemovalCardException("You can't remove that card");
    }

    public Card peek(){
        return cards.get(cards.size()-1);
    }

    public boolean isEmpty(){
        return cards.isEmpty();
    }

    public void reset(){
        cards.clear();
    }

    public void addAll(CardCascade c){
        cards.addAll(c.getCards());
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public CascadeType getType(){
        return CascadeType.CASCADE;
    }

    public int countInOrder(){
        if (isEmpty()) return 0;
        int count=1;
        for (int i=1;i<cards.size();i++){
            if (cards.get(cards.size()-i).getSuit().getColor()!=cards.get(cards.size()-i-1).getSuit().getColor()
                && cards.get(cards.size()-i).getFace().ordinal()+1==cards.get(cards.size()-i-1).getFace().ordinal()){
                count++;
            } else break;
        }
        return count;
    }

    @Override
    public String toString() {
        if(isEmpty()) return "-";
        return cards.get(0).toString();
    }
}
