package src.smash;

import src.genericGameObjects.Sprite;
import src.genericGameObjects.Updatable;

import java.awt.image.BufferedImage;

public class MenuBackground extends Sprite implements Updatable{
    private final int windowWidth, windowHeight;

    public MenuBackground(int wW, int wH, BufferedImage i) {
        super(wW / 2 - i.getWidth() / 2, wH / 2 - i.getHeight() / 2, i);
        windowWidth = wW;
        windowHeight = wH;
        velocity[0] = (int)(Math.random() * 4) * 2 - 3;
        velocity[2] = (int)(Math.random() * 2) * 2 - 1;
    }
    
    public void update() {
        if (left() >= -5 || right() <= windowWidth + 5) {
            velocity[0] *= -1;
        }
        if (top() >= -5 || bottom() <= windowHeight + 5) {
            velocity[2] *= -1;
        }
        displaceByVelocity();
    }
}
