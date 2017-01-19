package FreeCell.Card;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
        IMAGE_PATH = "images/";
        CLASS = Card.class;
        LOADER = CLASS.getClassLoader();
        PACKAGE_NAME = CLASS.getPackage().getName();
        java.net.URL imgURL = CLASS.getResource(IMAGE_PATH+"2c.png");
        ImageIcon image = new ImageIcon(imgURL);
        CARD_WIDTH=image.getIconWidth();
        CARD_HEIGHT=image.getIconHeight();
    }

    private CardFace face;
    private CardSuit suit;
    private ImageIcon cardImage;
    private Point2D position;
    private boolean highlighted=false;
    private boolean toExamine=false;

    public Card(CardFace face, CardSuit suit){
        this.face=face;
        this.suit=suit;
        position=new Point2D(0,0);
        java.net.URL imgURL = CLASS.getResource(IMAGE_PATH+face.getSymbol()+suit.getInitial()+".png");
        cardImage=new ImageIcon(imgURL);
        System.out.println(imgURL);
    }

    public static String getImagePath() {
        return IMAGE_PATH;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean getHighlighted(){
        return this.highlighted;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public CardFace getFace() {
        return face;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public void setPosition(int x, int y){
        position.setCoords(x,y);
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }


    public void draw(Graphics g)
    {

        cardImage.paintIcon(null,g,position.getX(),position.getY());
        if(highlighted){
        java.net.URL borderURL = CLASS.getResource(IMAGE_PATH+"border2.png");
        ImageIcon border=new ImageIcon(borderURL);
        border.paintIcon(null,g,position.getX()-(border.getIconWidth()-CARD_WIDTH)/2,position.getY()-27);
    }
    }

    public boolean isPressed(Point2D point){
        return (point.getX()<position.getX()+CARD_WIDTH && point.getX()>position.getX()
                && point.getY()>position.getY() && point.getY()<position.getY()+CARD_HEIGHT);
    }

    public void setX(int x) {
        position.setX(x);
    }

    public void setY(int y) {
        position.setY(y);
    }

    @Override
    public String toString() {
        return suit +" "+ face;
    }
}
