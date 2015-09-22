
package src.game;

import src.genericGameObjects.Updatable;
import src.input.InputHandler;

import java.awt.image.BufferedImage;

public abstract class GameTemplate implements Updatable {
    public int windowWidth, windowHeight;
    public BufferedImage backBuffer;
    public InputHandler input;
    
    public abstract void update();
    
    public abstract void render();
}
