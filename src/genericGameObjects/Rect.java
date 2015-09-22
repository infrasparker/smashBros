package src.genericGameObjects;

// A 2D object that stores an int array storing [left, right, width, height]
// and a velocity [left, right, up, down]
public class Rect implements Rectangular {
    protected final int[] rect = new int[4];
    protected final double[] velocity = {0, 0, 0, 0};
    
    public Rect() {
        this(0, 0, 0, 0);
    }
    
    public Rect(int x, int y, int w, int h) {
        rect[0] = x;
        rect[1] = y;
        rect[2] = w;
        rect[3] = h;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Information">
    public int left() {
        return rect[0];
    }
    
    public int right() {
        return rect[0] + rect[2];
    }
    
    public int top() {
        return rect[1];
    }
    
    public int bottom() {
        return rect[1] + rect[3];
    }
    
    public int width() {
        return rect[2];
    }
    
    public int height() {
        return rect[3];
    }
    
    public int[] center() {
         return new int[] {rect[0] + rect[2] / 2, rect[1] + rect[3] / 2};
    }
//</editor-fold>
    
    public void displaceByVelocity() {
        displace((int)(velocity[1] - velocity[0]), (int)(velocity[3] - velocity[2]));
    }
    
    public void changeSize(int w, int h) {
        rect[2] = w;
        rect[3] = h;
    }
    
    public void move(int x, int y) {
        rect[0] = x;
        rect[1] = y;
    }
    
    public void displace(int dx, int dy) {
        rect[0] += dx;
        rect[1] += dy;
    }
    
    public void setVelocity(String s, double v) {
        switch (s) {
            case "left":
                velocity[0] = v;
                break;
            case "right":
                velocity[1] = v;
                break;
            case "up":
                velocity[2] = v;
                break;
            case "down":
                velocity[3] = v;
                break;
        }
    }
}
