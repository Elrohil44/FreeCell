package FreeCell;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import FreeCell.Card.*;
import FreeCell.Card.Point2D;
import FreeCell.Cascades.*;
import FreeCell.Exceptions.EndGameException;
import FreeCell.Exceptions.WonGameException;
import FreeCell.Exceptions.WrongMoveException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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


    private static final int TABLEAU_TOP = GAP + FOUNDATION_BOTTOM;
    private static final int TABLEAU_INCR_Y  = 25;
    private static final int TABLEAU_START_X = GAP;
    private static final int TABLEAU_INCR_X  = Card.CARD_WIDTH + GAP;

    private static final int DISPLAY_WIDTH = GAP + NUMBER_OF_PILES * TABLEAU_INCR_X;
    private static final int DISPLAY_HEIGHT = TABLEAU_TOP + 3 * Card.CARD_HEIGHT + GAP;
    private static final int FOUNDATION_LEFT = DISPLAY_WIDTH-GAP- 4*Card.CARD_WIDTH;

    private static final Color BACKGROUND_COLOR = new Color(0, 200, 0);

    private Point2D dragFrom = new Point2D(0,0);
    private Point2D movedFrom;
    private boolean selected=false;

    private Card highlighted = null;
    private Card draggedCard = null;
    private Card toExamine = null;
    private CardCascade draggedFromCascade = null;
    private int button;
    private boolean draggable;

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

        if (toExamine != null){
            toExamine.draw(g);
        }
    }

    private void drawCascade(Graphics g,CardCascade cascade,boolean onlyTop){
        Rectangle rec = whereIs.get(cascade);
        g.drawRect(rec.x,rec.y,rec.width,rec.height);
        int y = rec.y;
        if (draggedFromCascade==null && !cascade.isEmpty()){
            cascade.peek().setHighlighted(false);
        }
        else if (!cascade.isEmpty()){
            cascade.peek().setHighlighted(cascade == draggedFromCascade);
        }
        if(cascade.isHighlighted()) {
            if (!cascade.isEmpty()) {
                cascade.peek().setHighlighted(true);
            } else {
                java.net.URL borderURL = Card.class.getResource(Card.getImagePath()+"border2.png");
                ImageIcon border=new ImageIcon(borderURL);
                border.paintIcon(null,g,rec.x-(border.getIconWidth()-rec.width)/2,rec.y-27);
                cascade.setHighlighted(false);
            }
        }
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
                    if(c!= draggedCard && c!=toExamine){
                        c.setPosition(rec.x, y);
                        c.draw(g);
                        y+=TABLEAU_INCR_Y;
                    }
                }

            }
        }
        if (cascade.isHighlighted() && !cascade.isEmpty()){
            cascade.peek().setHighlighted(false);
            cascade.setHighlighted(false);
        }
    }


    public void mousePressed(MouseEvent e){
        if(!model.isWaiting()) {
            movedFrom = new Point2D(e.getX(), e.getY());
            toExamine = null;
            draggable = false;
            draggedCard = null;
            CardCascade onto = findCascadeAt(movedFrom);
            if (onto == null) return;
            if (draggedFromCascade != null && draggedFromCascade != onto && e.getButton() == 1) {
                try {
                    model.move(draggedFromCascade, onto, true);
                } catch (WrongMoveException | WonGameException | EndGameException exc) {
                    Notification note = new Notification(exc.getMessage());
                }
                draggedFromCascade = null;
                selected = false;
                this.repaint();
                return;
            }


            if (onto.canRemove() && !onto.isEmpty()) {
                Card testCard = onto.peek();
                draggedCard = testCard;
                if (testCard.isPressed(movedFrom)) {
                    dragFrom.setCoords(movedFrom.getX() - testCard.getX(), movedFrom.getY() - testCard.getY());
                    if (e.getButton() == 1) draggable = true;
                }
            }
            if (!draggable && draggedFromCascade == onto && e.getButton() == 1) {
                draggedFromCascade = null;
                return;
            }
            if (draggable && draggedFromCascade == onto && e.getButton() == 1) {
                selected = false;
            } else selected = true;
            if (onto.getType() == CascadeType.FOUNDATION) return;
            if (!onto.isEmpty()) draggedFromCascade = onto;
            if (draggedCard != null) {
                if ((e.getButton() == 3 || e.getClickCount() % 2 == 0) && draggedCard.isPressed(movedFrom)) {
                    rightClick();
                } else if (e.getButton() == 3 && !draggedCard.isPressed(movedFrom)) {
                    for (Card c : draggedFromCascade.reverseIterate()) {
                        if (c.isPressed(movedFrom)) {
                            draggedFromCascade = null;
                            toExamine = c;
                            this.repaint();
                            break;
                        }

                    }
                }
            }
        }
    }

    public void rightClick(){
        //if(draggedCard==null) return;
        if(draggedFromCascade.getType()== CascadeType.CELL){
            try {
                model.moveToFoundation(draggedFromCascade);
            } catch(WrongMoveException|WonGameException|EndGameException exc){
                Notification note = new Notification(exc.getMessage());
            }
        }
        else if(draggedFromCascade.getType()==CascadeType.TABLEAU){
            try{
                model.moveToCell(draggedFromCascade);
            } catch(WonGameException|EndGameException exc){
                Notification note = new Notification(exc.getMessage());
            } catch(WrongMoveException exc){
                try{
                    model.moveToFoundation(draggedFromCascade);
                } catch(WrongMoveException|WonGameException|EndGameException exc2){
                    Notification note = new Notification(exc2.getMessage());
                }
            }
        }
        draggedFromCascade=null;
    }

    public void stateChanged(ChangeEvent e){

        if(draggedFromCascade!=null && !draggedFromCascade.isEmpty()) draggedFromCascade.peek().setHighlighted(false);
        draggedFromCascade=null;
        clearDrag();
        this.repaint();
    }

    public void mouseDragged(MouseEvent e){
        if(draggable) {
            if (draggedCard == null) {
                return;
            }
            Point2D point = new Point2D(e.getX()-dragFrom.getX(),e.getY()-dragFrom.getY());

            //x=Math.min(Math.max(x,0),getWidth() - Card.CARD_WIDTH);
            //y=Math.min(Math.max(y,0),getHeight() - Card.CARD_HEIGHT);

            draggedCard.setPosition(point);

            this.repaint();
        }
    }

    public void mouseReleased(MouseEvent e){
        if(!model.isWaiting()) {
            if (draggedFromCascade != null) {
                Point2D point = new Point2D(e.getX(), e.getY());

                CardCascade onto = findCascadeAt(point);
                if (onto != null && draggable) {
                    try {
                        model.move(draggedFromCascade, onto, false);
                    } catch (WrongMoveException | WonGameException | EndGameException exc) {
                        if (draggedFromCascade != onto) {
                            Notification note = new Notification(exc.getMessage());
                        }
                    }
                }
                if (onto != draggedFromCascade) {
                    draggedFromCascade = null;
                } else if (onto == draggedFromCascade && point.distanceTo(movedFrom) < 15 && !selected) {
                    draggedFromCascade = null;
                }

            }
            clearDrag();
            this.repaint();
        }
    }

    private void clearDrag(){
        if (draggedCard!=null) draggedCard.setHighlighted(false);
        draggedCard=null;
        toExamine=null;
        //draggedFromCascade=null;
    }

    private CardCascade findCascadeAt(Point2D p){
        Point2D point = new Point2D(p.getX(),p.getY());
        if (p.getY()>TABLEAU_TOP && p.getY()<DISPLAY_HEIGHT) point.setY(TABLEAU_TOP+1);
        for(CardCascade c: model.iterate()){
            Rectangle rec = whereIs.get(c);
            if (rec.contains(point.getX(),point.getY())) return c;
        }
        return null;
    }



    public void mouseMoved (MouseEvent e) {}
    public void mouseEntered(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mouseExited (MouseEvent e){}
}
