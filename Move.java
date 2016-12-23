package FreeCell;

/**
 * Created by Wiesiek on 2016-12-23.
 */
public class Move {
    private CardCascade from;
    private CardCascade onto;

    public Move(CardCascade from,CardCascade onto){
        this.from=from;
        this.onto=onto;
    }

    public CardCascade getFrom() {
        return from;
    }

    public CardCascade getOnto() {
        return onto;
    }
}
