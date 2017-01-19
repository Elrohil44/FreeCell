package FreeCell;

import FreeCell.Cascades.CardCascade;

/**
 * Created by Wiesiek on 2016-12-23.
 */
public class Move {
    private CardCascade from;
    private CardCascade onto;
    private int count;

    public Move(CardCascade from,CardCascade onto, int count){
        this.from=from;
        this.onto=onto;
        this.count=count;
    }

    public CardCascade getFrom() {
        return from;
    }

    public CardCascade getOnto() {
        return onto;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return from.toString()+" "+onto.toString()+" "+count;
    }
}
