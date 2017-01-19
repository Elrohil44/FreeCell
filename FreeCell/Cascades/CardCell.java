package FreeCell.Cascades;

import FreeCell.Card.*;
/**
 * Created by Wiesiek on 2016-12-22.
 */
public class CardCell extends CardCascade {
    @Override
    public boolean canAdd(Card card){
        return isEmpty();
    }

    @Override
    public CascadeType getType(){
        return CascadeType.CELL;
    }
}
