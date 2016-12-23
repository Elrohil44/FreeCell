package FreeCell;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.IdentityHashMap;

/**
 * Created by Wiesiek on 2016-12-22.
 */
public class UICard extends JComponent implements MouseListener,MouseMotionListener,ChangeListener {
    private static final int NUMBER_OF_PILES = 8;

    //... Constants specifying position of display elements
    private static final int GAP = 10;
    private static final int FOUNDATION_TOP = GAP;
    private static final int FOUNDATION_BOTTOM = FOUNDATION_TOP + Card.CARD_HEIGHT;
    private static final int FOUNDATION_LEFT = 5*GAP + 4*Card.CARD_WIDTH;

    private static final int TABLEAU_TOP = GAP + FOUNDATION_BOTTOM;
    private static final int TABLEAU_INCR_Y  = 25;
    private static final int TABLEAU_START_X = GAP;
    private static final int TABLEAU_INCR_X  = Card.CARD_WIDTH + GAP;

    private static final int DISPLAY_WIDTH = GAP + NUMBER_OF_PILES * TABLEAU_INCR_X;
    private static final int DISPLAY_HEIGHT = TABLEAU_TOP + 3 * Card.CARD_HEIGHT + GAP;

    private static final Color BACKGROUND_COLOR = new Color(0, 200, 0);

    private int dragFromX = 0;
    private int dragFromY = 0;

    private Card draggedCard = null;
    private CardCascade draggedFromCascade = null;

    private IdentityHashMap<CardCascade, Rectangle> whereIs = new IdentityHashMap<CardCascade,Rectangle>();
    private GameModel model;

    public UICard(GameModel model){
        this.model=model;

        setPreferredSize(new Dimension(DISPLAY_WIDTH,DISPLAY_HEIGHT));
        setBackground(BACKGROUND_COLOR);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        for (int i=0,x=GAP;i<4;i++,x+=Card.CARD_WIDTH){
            CardCascade c = model.getCardCell(i);
            whereIs.put(c,new Rectangle(x,FOUNDATION_TOP,Card.CARD_WIDTH,Card.CARD_HEIGHT));
        }

        for (int i=0,x=FOUNDATION_LEFT;i<4;i++,x+=Card.CARD_WIDTH){
            CardCascade c = model.getCardFoundation(i);
            whereIs.put(c,new Rectangle(x,FOUNDATION_TOP,Card.CARD_WIDTH,Card.CARD_HEIGHT));
        }

        for(int i=0,x = TABLEAU_START_X; i<NUMBER_OF_PILES;i++,x+=TABLEAU_INCR_X){
            CardCascade c = model.getCardTableau(i);
            whereIs.put(c,new Rectangle(x,TABLEAU_TOP,Card.CARD_WIDTH,Card.CARD_HEIGHT));
        }

        model.addChangeListener(this);
    }

    @Override
    public void paintComponent(Graphics g){
        int width = getWidth();
        int height = getHeight();
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0,0,width,height);
        g.setColor(Color.BLACK);

        for (CardTableau t: model.getCardTableaus()){
            drawCascade(g,t,false);
        }

        for (CardCell c: model.getCardCells()){
            drawCascade(g,c,true);
        }

        for (CardFoundation f: model.getCardFoundations()){
            drawCascade(g,f,true);
        }

        if (draggedCard != null){
            draggedCard.draw(g);
        }
    }

    private void drawCascade(Graphics g,CardCascade cascade,boolean onlyTop){
        Rectangle rec = whereIs.get(cascade);
        g.drawRect(rec.x,rec.y,rec.width,rec.height);
        int y = rec.y;
        if(!cascade.isEmpty()){
            if(onlyTop) {
                Card c = cascade.peek();
                if (c != draggedCard) {
                    c.setPosition(rec.x, y);
                    c.draw(g);
                }
            }
            else{
                for (Card c: cascade.iterate()){
                    if(c!= draggedCard){
                        c.setPosition(rec.x, y);
                        c.draw(g);
                        y+=TABLEAU_INCR_Y;
                    }
                }

            }
        }
    }

    public void mousePressed(MouseEvent e){
        int x = e.getX();
        int y = e.getY();

        draggedCard = null;
        for (CardCascade cascade: model.iterate()){
            if(cascade.canRemove() && !cascade.isEmpty()){
                Card testCard = cascade.peek();
                if(testCard.isPressed(x,y)){
                    dragFromX = x - testCard.getX();
                    dragFromY = y - testCard.getY();
                    draggedCard = testCard;
                    draggedFromCascade = cascade;
                    break;
                }
            }
        }
    }

    public void stateChanged(ChangeEvent e){
        clearDrag();
        this.repaint();
    }

    public void mouseDragged(MouseEvent e){
        if(draggedCard == null){
            return;
        }
        int x = e.getX() - dragFromX;
        int y = e.getY() - dragFromY;

        x=Math.min(Math.max(x,0),getWidth() - Card.CARD_WIDTH);
        y=Math.min(Math.max(y,0),getHeight() - Card.CARD_HEIGHT);

        draggedCard.setPosition(x,y);

        this.repaint();

    }

    public void mouseReleased(MouseEvent e){
        if(draggedFromCascade != null){
            int x = e.getX();
            int y = e.getY();

            CardCascade onto = findCascadeAt(x,y);
            if(onto != null);{
                model.move(draggedFromCascade,onto);
            }

            clearDrag();
            this.repaint();
        }
    }

    private void clearDrag(){
        draggedCard=null;
        draggedFromCascade=null;
    }

    private CardCascade findCascadeAt(int x, int y){
        if (y>TABLEAU_TOP && y<DISPLAY_HEIGHT) y = TABLEAU_TOP+1;
        for(CardCascade c: model.iterate()){
            Rectangle rec = whereIs.get(c);
            if (rec.contains(x,y)) return c;
        }
        return null;
    }

    public void mouseMoved (MouseEvent e) {}
    public void mouseEntered(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mouseExited (MouseEvent e){}
}
