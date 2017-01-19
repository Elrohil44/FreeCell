package FreeCell.Cascades;

import FreeCell.Card.*;
/**
 * Created by Wiesiek on 2016-12-22.
 */
public class CardFoundation extends CardCascade{
    @Override
    public boolean canRemove(){
        return false;
    }

    @Override
    public boolean canAdd(Card card){
        if (isEmpty() && card.getFace()==CardFace.ACE) return true;
        if (isEmpty() && card.getFace()!=CardFace.ACE) return false;
        Card t = peek();
        return (!isEmpty() && t.getSuit()==card.getSuit() && t.getFace().ordinal()+1==card.getFace().ordinal());
    }

    public CardSuit getSuit(){
        if (isEmpty()) return null;
        return peek().getSuit();
    }

    @Override
    public CascadeType getType(){
        return CascadeType.FOUNDATION;
    }
}
