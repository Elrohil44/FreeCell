package FreeCell;

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
        Card t = peek();
        return (!isEmpty() && t.getSuit()==card.getSuit() && t.getFace().ordinal()+1==card.getFace().ordinal());
    }
}
