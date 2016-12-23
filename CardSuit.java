package FreeCell;

import java.awt.*;

/**
 * Created by Wiesiek on 2016-12-22.
 */
enum CardSuit {
    Spades(Color.BLACK,'s'),
    Hearts(Color.RED,'h'),
    Diamonds(Color.RED,'d'),
    Clubs(Color.BLACK,'c');

    private final Color color;
    private final char initial;

    CardSuit(Color color,char initial){
        this.color=color;
        this.initial=initial;
    }

    public Color getColor(){
        return color;
    }

    public char getInitial() {
        return initial;
    }
}
