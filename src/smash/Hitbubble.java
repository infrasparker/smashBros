package src.smash;

import src.genericGameObjects.Circular;

import java.io.File;

public class Hitbubble implements Circular {
    public int[] center;
    private int radius;
    private final int priority;
    private int angle;
    private final int damage, baseKB;
    private final double kBScaling;
    private String sfx;
    
    public Hitbubble(int x, int y, int r, int d, int bKB, double kBS,
            int a, int p, String s) {
        center = new int[] {x, y};
        radius = r;
        damage = d;
        baseKB = bKB;
        kBScaling = kBS;
        angle = a;
        priority = p;
        sfx = s;
    }
    
    public Hitbubble(Hitbubble h) {
        this(h.center()[0], h.center()[1], h.radius(), h.damage(), h.baseKB(),
                h.baseKB(), h.angle(), h.priority(), h.sfx());
    }
    
    public void resize(double s) {
        center[0] *= s;
        center[1] *= s;
        radius *= s;
    }
    
    public void flipAngle() {
        angle = 180 - angle;
    }
    
    public int radius() {
        return radius;
    }
    
    public int[] center() {
        return center;
    }
    
    public int damage() {
        return damage;
    }
    
    public int baseKB() {
        return baseKB;
    }
    
    public double kBScaling() {
        return kBScaling;
    }
    
    public int angle() {
        return angle;
    }
    
    public int priority() {
        return priority;
    }
    
    public String sfx() {
        return sfx;
    }
}
