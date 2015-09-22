package src.smash;

import src.genericGameObjects.Sprite;
import src.image.ImageHandler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Camera {
    private final int windowWidth, windowHeight, minWidth, minHeight;
    private final int stageW, stageH;
    private final ArrayList<Sprite> targets, entities;
    private final BufferedImage image;
    private Stage stage;
    
    public Camera(int wW, int wH, int mW, Stage s) {
        this(wW, wH, mW, s.width(), s.height());
        stage = s;
    }
    
    public Camera(int wW, int wH, int mW, int w, int h) {
        windowWidth = wW;
        windowHeight = wH;
        stageW = w;
        stageH = h;
        minWidth = mW;
        minHeight = (int)((double)mW * windowHeight / windowWidth);
        image = new BufferedImage(windowWidth, windowHeight,
                BufferedImage.TYPE_INT_ARGB);
        targets = new ArrayList();
        entities = new ArrayList();
    }
    
    public void addTarget(Sprite s) {
        targets.add(s);
    }
    
    public void addTargets(ArrayList<Sprite> s) {
        targets.addAll(s);
    }
    
    public void addEntities(ArrayList<Sprite> s) {
        entities.addAll(s);
    }
    
    public void addEntity(Sprite s) {
        entities.add(s);
    }
    
    public void addToAll(Sprite s) {
        if (!targets.contains(s)) {
            targets.add(s);
        }
        if (!entities.contains(s)) {
            entities.add(s);
        }
    }
    
    public void removeFromAll(Sprite s) {
        if (targets.contains(s)) {
            targets.remove(s);
        }
        if (entities.contains(s)) {
            entities.remove(s);
        }
    }
    
    public BufferedImage capture() {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, windowWidth, windowHeight);
        int lBound, rBound, tBound, bBound;
        if (targets.size() > 0) {
            lBound = targets.get(0).left();
            rBound = targets.get(0).right();
            tBound = targets.get(0).top();
            bBound = targets.get(0).bottom();
        }
        else {
            lBound = stage.platforms().get(0).left();
            rBound = stage.platforms().get(0).right();
            tBound = stage.platforms().get(0).top() - 100;
            bBound = stage.platforms().get(0).bottom() - 100;
            for (Sprite s : stage.platforms()) {
                lBound = Math.min(s.left(), lBound);
                rBound = Math.max(s.right(), rBound);
                tBound = Math.min(s.top(), tBound);
                bBound = Math.max(s.bottom() - 100, bBound);
            }
        }
        for (Sprite s : targets) {
            if (s instanceof DeathExplosion) {
                DeathExplosion d = (DeathExplosion)s;
                lBound = Math.min(d.totalBounds()[0], lBound);
                rBound = Math.max(d.totalBounds()[1], rBound);
                tBound = Math.min(d.totalBounds()[2], tBound);
                bBound = Math.max(d.totalBounds()[3], bBound);
            }
            else {
                lBound = Math.min(s.left() - 100, lBound);
                rBound = Math.max(s.right() + 100, rBound);
                tBound = Math.min(s.top(), tBound);
                bBound = Math.max(s.bottom(), bBound);
            }
        }
        int width = rBound - lBound, height = bBound - tBound;
        if (width < minWidth) {
            lBound = (rBound + lBound) / 2 - minWidth / 2;
            rBound = (rBound + lBound) / 2 + minWidth / 2;
            width = rBound - lBound;
        }
        if (height < minHeight) {
            tBound = (bBound + tBound) / 2 - minHeight / 2;
            bBound = (bBound + tBound) / 2 + minHeight / 2;
            height = bBound - tBound;
        }
        double rW = (double)windowWidth / width;
        double rH = (double)windowHeight / height;
        double r;
        if (rW < rH) {
            r = rW;
            tBound = (bBound + tBound - width * windowHeight / windowWidth) / 2;
            bBound = (bBound + tBound + width * windowHeight / windowWidth) / 2;
        }
        else {
            r = rH;
            lBound = (rBound + lBound - height * windowWidth / windowHeight) / 2;
            rBound = (rBound + lBound + height * windowWidth / windowHeight) / 2;
        }
        int hPara = (int)((stageW - rBound - lBound) / (2d * stageW) *
                Math.abs(windowWidth - stage.background().getWidth()) / 2);
        int vPara = (int)((stageH - tBound - bBound) / (2d * stageH) *
                Math.abs(windowHeight - stage.background().getHeight()) / 2);
        g.drawImage(stage.background(), (windowWidth - stage.background().getWidth()) / 2 + hPara,
                (windowHeight - stage.background().getHeight()) / 2 + vPara, null);
        for (Sprite s : entities) {
            int newL = (int)((s.left() - lBound) * r);
            int newT = (int)((s.top() - tBound) * r);
            int newW = (int)(s.width() * r);
            if (s instanceof SmashCharacter) {
                SmashCharacter c = (SmashCharacter)s;
                if (!c.timersContains("respawn")) {
                    g.drawImage(ImageHandler.resize(s.fullImage(), newW), newL, newT, null);
                    // this section is used to draw hitbubbles.
                    if (c.hitbubbles() != null) {
                        for (Hitbubble h : c.hitbubbles()) {
                            int x = (int)((c.left() + h.center()[0] - h.radius() - lBound) * r);
                            int y = (int)((c.top() + h.center()[1] - h.radius() - tBound) * r);
                            int d = (int)((2 * h.radius()) * r);
                            g.setColor(Color.red);
                            g.drawOval(x, y, d, d);
                        }
                    }
                }
            }
            else {
                g.drawImage(ImageHandler.resize(s.fullImage(), newW), newL, newT, null);
            }
        }
        g.dispose();
        return image;
    }
}
