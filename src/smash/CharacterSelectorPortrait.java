package src.smash;

import src.genericGameObjects.Sprite;

import java.awt.image.BufferedImage;

public class CharacterSelectorPortrait extends Sprite {
    public String name;

    public CharacterSelectorPortrait(int x, int y, BufferedImage i, String n) {
        super(x, y, i);
        name = n;
    }
    
    public String toString() {
        return "CSP: " + name;
    }
}
