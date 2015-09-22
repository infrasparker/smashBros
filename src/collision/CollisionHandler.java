package src.collision;

import src.genericGameObjects.Circular;
import src.genericGameObjects.Rectangular;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class CollisionHandler {
    //<editor-fold defaultstate="collapsed" desc="detectRectangularCircular">
    public static boolean detectRectangularCircular(Rectangular a, int[] c,
            int r) {
        return detectRectangularCircular(a, c[0], c[1], r);
    }
    
    public static boolean detectRectangularCircular(Rectangular a, Circular b) {
        return detectRectangularCircular(a, b.center(), b.radius());
    }
    public static boolean detectRectangularCircular(Rectangular a, int cx,
            int cy, int r) {
        boolean cInR = detectPointInRectangular(cx, cy, a);
        boolean topInt = detectLineInCircular(a.left(), a.right(), a.top(),
                a.top(), cx, cy, r);
        boolean botInt = detectLineInCircular(a.left(), a.right(), a.bottom(),
                a.bottom(), cx, cy, r);
        boolean rightInt = detectLineInCircular(a.right(), a.right(), a.top(),
                a.bottom(), cx, cy, r);
        boolean leftInt = detectLineInCircular(a.left(), a.left(), a.bottom(),
                a.top(), cx, cy, r);
//        System.out.println(cInR + " " + topInt + " " + botInt + " " + rightInt +
//                " " + leftInt);
        return cInR || topInt || botInt || rightInt || leftInt;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="detectPointInRectangular">
    public static boolean detectPointInRectangular(int ax, int ay, Rectangular b) {
        return b.left() <= ax && ax <= b.right() && b.top() <= ay &&
                ay <= b.bottom();
    }
    
    public static boolean detectPointInRectangular(int[] a, Rectangular b) {
	return detectPointInRectangular(a[0], a[1], b);
    }
//</editor-fold>
    
    public static boolean detectRectangular(Rectangular a, Rectangular b) {
        return a.left() < b.right() && a.right() > b.left() &&
                a.top() < b.bottom() && a.bottom() > b.top();
    }
    
    //<editor-fold defaultstate="collapsed" desc="detectCircular">
    public static boolean detectCircular(Circular a, Circular b) {
	return detectCircular(a.center(), b.center(), a.radius(), b.radius());
    }
    
    public static boolean detectCircular(int[] c1, int[] c2, int r1, int r2) {
        return detectCircular(c1[0], c1[1], c2[0], c2[1], r1, r2);
    }
    
    public static boolean detectCircular(int x1, int y1, int x2, int y2, int r1,
            int r2) {
        return (int)(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)))
                <= r1 + r2;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="detectLineInCircular">
    public static boolean detectLineInCircular(int ax, int ay, int bx, int by,
            Circular c) {
        return detectLineInCircular(ax, ay, bx, by, c.center(), c.radius());
    }
    
    public static boolean detectLineInCircular(int ax, int ay, int bx, int by,
            int[] c, int r) {
        return detectLineInCircular(ax, ay, bx, by, c[0], c[1], r);
        
    }
    
    public static boolean detectLineInCircular(int ax, int ay, int bx, int by,
            int cx, int cy, int r) {
        Line2D line  = new Line2D.Float(ax, bx, ay, by);
        Point2D center = new Point2D.Float(cx, cy);
        return line.ptSegDist(center) <= r;
    }
//</editor-fold>
}
