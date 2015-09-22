package src.smash;

import src.genericGameObjects.SongStorage;
import src.genericGameObjects.Sprite;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Stage {
    protected int width, height;
    protected String name;
    protected final ArrayList<Sprite> solids, semiSolids, platforms;
    protected final int[][] spawnPoints;
    protected int[][] respawnPoints;
    protected BufferedImage background;
    
    public Stage(int w, int h, String n) {
        name = n;
        width = w;
        height = h;
        solids = new ArrayList();
        semiSolids = new ArrayList();
        platforms = new ArrayList();
        spawnPoints = new int[2][2];
        respawnPoints = new int[3][2];
        generateStage();
    }
    
    protected abstract void generateStage();
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    public int[][] spawnPoints() {
        return spawnPoints;
    }
    
    public ArrayList<Sprite> solids() {
        return solids;
    }
    
    public ArrayList<Sprite> semiSolids() {
        return semiSolids;
    }
    
    public ArrayList<Sprite> platforms() {
        return platforms;
    }
    
    public int[][] respawnPoints() {
        return respawnPoints;
    }
    
    public String name() {
        return name;
    }
    
    public BufferedImage background() {
        return background;
    }
}
