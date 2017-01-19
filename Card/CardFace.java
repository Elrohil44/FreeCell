package FreeCell.Card;

/**
 * Created by Wiesiek on 2016-12-22.
 */
public enum CardFace {
    ACE('a'),
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    TEN('t'),
    JACK('j'),
    QUEEN('q'),
    KING('k');

    private final char symbol;

    CardFace(char symbol){
        this.symbol=symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}
