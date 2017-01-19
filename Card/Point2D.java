package FreeCell.Card;

/**
 * Created by Wiesiek on 2017-01-18.
 */
public class Point2D {
    private int x;
    private int y;

    public Point2D(int x, int y){
        this.x=x;
        this.y=y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setCoords(int x, int y){
        this.x=x;
        this.y=y;
    }

    public String toString() {
        return "("+x+", "+y+")";
    }

    public double distanceTo(Point2D p){
        return Math.sqrt((p.getX()-x)*(p.getX()-x)+(p.getY()-y)*(p.getY()-y));
    }
}
