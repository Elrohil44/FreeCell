package FreeCell;

import java.util.*;

/**
 * Created by Wiesiek on 2016-12-22.
 */
public class CardCascade {
    private ArrayList<Card> cards = new ArrayList<Card>();

    public void forcePush(Card card){
        cards.add(card);
    }

    public Card forcePop(){
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
        /*if (cards.isEmpty()) return true;
        Card c = cards.get(cards.size()-1);
        if (c.getSuit().getColor()!=card.getSuit().getColor()){
            if(card.getFace().ordinal()+1 == c.getFace().ordinal()) return true;
        }
        return false;*/
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
}
