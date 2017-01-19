package FreeCell.Cascades;

import FreeCell.Card.*;
/**
 * Created by Wiesiek on 2016-12-22.
 */
public class CardTableau extends CardCascade{

    @Override
    public boolean canAdd(Card card){
        if(isEmpty()) return true;
        Card t=peek();
        return (t.getFace().ordinal()-1==card.getFace().ordinal() && t.getSuit().getColor() != card.getSuit().getColor());
    }

    @Override
    public CascadeType getType(){
        return CascadeType.TABLEAU;
    }
}
