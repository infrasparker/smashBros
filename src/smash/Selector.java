package src.smash;

import src.audio.SFXHandler;
import src.collision.CollisionHandler;
import src.genericGameObjects.*;
import src.image.ImageHandler;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

public class Selector extends Manipulable implements Circular {
    private boolean locked;
    private String currentChar;
    private final int speed;
    private final int radius;
    public ArrayList<CharacterSelectorPortrait> csps;
    
    public Selector(int x, int y, BufferedImage i) {
        super(x, y, ImageHandler.resize(i, 30));
        locked = false;
        speed = 10;
        radius = 15;
        currentChar = null;
    }
    
    public void keyTrigger(Map<String, Boolean> keys) {
        for (String key : keys.keySet()) {
            String[] x = {"left", "right", "up", "down"};
            for (int i = 0; i < x.length; i++) {
                if (key.equals(x[i])) {
                    changeVelocity(i, keys.get(key));
                }
            }
            x = new String[] {"a", "b"};
            for (int i = 0; i < x.length; i++) {
                if (key.equals(x[i])) {
                    triggerAction(i, keys.get(key));
                }
            }
        }
    }
    
    private void changeVelocity(int d, boolean o) {
        if (o) {
           velocity[d] = speed;
        }
        else {
            velocity[d] = 0;
        }
    }
    
    private void triggerAction(int a, boolean o) {
        if (o) {
            if (a == 0) {
                if (currentChar != null && !locked) {
                    SFXHandler.play("smash\\audio\\sfx\\menu\\misc\\charSet.wav");
                    SFXHandler.play("smash\\audio\\sfx\\menu\\names\\" + currentChar + ".wav");
                    locked = true;
                }
            }
            else if (a == 1) {
                if (currentChar != null && locked) {
                    SFXHandler.play("smash\\audio\\sfx\\menu\\misc\\charCancel.wav");
                    locked = false;
                }
            }
        }
    }
    
    public void update() {
        CharacterSelectorPortrait csp = null;
        for (CharacterSelectorPortrait c : csps) {
            if (CollisionHandler.detectPointInRectangular(center(), c)) {
                csp = c;
            }
        }
        if (csp != null) {
            currentChar = csp.name;
        }
        else {
            currentChar = null;
        }
        if (!locked) {
            displaceByVelocity();
        }
    }

    public int radius() {
	return radius;
    }
    
    public String currentChar() {
        return currentChar;
    }
    
    public boolean locked() {
        return locked;
    }
}
