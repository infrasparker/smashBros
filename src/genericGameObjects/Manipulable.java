package src.genericGameObjects;

import java.awt.image.BufferedImage;

public abstract class Manipulable extends Sprite implements Controllable {
    public Manipulable(BufferedImage i) {
        super(i);
    }
    
    public Manipulable(BufferedImage i, boolean u) {
        super(i, u);
    }

    public Manipulable(int x, int y, BufferedImage i) {
        super(x, y, i);
    }
}
