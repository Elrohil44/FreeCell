package FreeCell;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Wiesiek on 2016-12-22.
 */
public class Card {
    private static final String IMAGE_PATH;
    private static final Class <?> CLASS;
    private static final String PACKAGE_NAME;
    private static final ClassLoader LOADER;
    public static final int CARD_WIDTH;
    public static final int CARD_HEIGHT;
    static{
        IMAGE_PATH = "/images/";
        CLASS = Card.class;
        LOADER = CLASS.getClassLoader();
        PACKAGE_NAME = CLASS.getPackage().getName();
        java.net.URL imgURL = LOADER.getResource(PACKAGE_NAME+IMAGE_PATH+"2c.png");
        ImageIcon image = new ImageIcon(imgURL);
        CARD_WIDTH=image.getIconWidth();
        CARD_HEIGHT=image.getIconHeight();
    }

    private CardFace face;
    private CardSuit suit;
    private ImageIcon cardImage;
    private int x,y;



    public Card(CardFace face, CardSuit suit){
        this.face=face;
        this.suit=suit;
        x=y=0;
        java.net.URL imgURL = LOADER.getResource(PACKAGE_NAME+IMAGE_PATH+face.getSymbol()+suit.getInitial()+".png");
        cardImage=new ImageIcon(imgURL);
        System.out.println(imgURL);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public CardFace getFace() {
        return face;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public void setPosition(int x, int y){
        this.x=x;
        this.y=y;
    }

    public void draw(Graphics g){
        cardImage.paintIcon(null,g,x,y);
    }

    public boolean isPressed(int x, int y){
        return (x<this.x+CARD_WIDTH && x>this.x && y>this.y && y<this.y+CARD_HEIGHT);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
