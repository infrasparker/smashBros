package src.genericGameObjects;

import src.image.ImageHandler;

import java.awt.image.BufferedImage;

// A type of Rect that also contains a BufferedImage "img" that can be drawn.
public class Sprite extends Rect {
    protected BufferedImage image, fullImage;
    protected boolean useImage;
    
    public Sprite(BufferedImage i) {
        this(0, 0, i);
    }
    
    public Sprite(BufferedImage i, boolean u) {
        this(0, 0, 0, 0, i, u);
    }
    
    public Sprite(int x, int y, BufferedImage i) {
        this(x, y, i.getWidth(), i.getHeight(), i, true);
    }
    
    public Sprite(int x, int y, int w, BufferedImage i, boolean u) {
        this(x, y, w, (int)((double)w * i.getHeight() / i.getWidth() + .5),
                i, u);
    }
    
    public Sprite(int x, int y, int w, int h, BufferedImage i, boolean u) {
        super(x, y, w, h);
        useImage = u;
        if (useImage) {
            image = ImageHandler.resize(i, w);
            fullImage = null;
        }
        else {
            image = null;
            fullImage = i;
        }
    }
    
    public void changeImage(BufferedImage i) {
        if (useImage) {
            rect[2] = i.getWidth();
            rect[3] = i.getHeight();
            image = i;
        }
        else {
            fullImage = i;
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Information">
    public BufferedImage image() {
        return image;
    }
    
    public BufferedImage fullImage() {
        return fullImage;
    }
//</editor-fold>
}
