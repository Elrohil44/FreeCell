package FreeCell.Cascades;

import FreeCell.Card.*;
/**
 * Created by Wiesiek on 2016-12-22.
 */
public class Deck extends CardCascade {
    public Deck(){
        for (CardSuit s: CardSuit.values()){
            for (CardFace f: CardFace.values()){
                push(new Card(f,s));
            }
        }
        shuffle();
    }

    @Override
    public CascadeType getType(){
        return CascadeType.DECK;
    }
}
