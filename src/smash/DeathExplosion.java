package src.smash;

import src.audio.SFXHandler;
import src.genericGameObjects.Sprite;
import src.genericGameObjects.TimeStorage;
import src.genericGameObjects.Updatable;
import src.image.ImageHandler;

import java.awt.image.BufferedImage;
import java.util.Map;

public class DeathExplosion extends Sprite implements Updatable {
    private TimeStorage timer;
    private Map<String, BufferedImage> imageData;
    private String direction;
    public int[] deathPoint;
    
    // x and y are location of SCD upon death.
    public DeathExplosion(int x, int y, Map<String, BufferedImage> imgData, String d) {
        super(0, 0, 400, imgData.get("1.png"), false);
        timer = new TimeStorage(60);
        imageData = imgData;
        direction = d;
        deathPoint = new int[] {x, y};
        SFXHandler.play("smash\\audio\\sfx\\battle\\misc\\death.wav");
        changeImageAndMove(imageData.get("1.png"));
    }
    
    public void update() {
        timer.update();
        if (timer.timer == 4) {
            changeImageAndMove(imageData.get("5.png"));
        }
        else if ((timer.timer % 4 == 0) && (timer.timer != 0)) {
            changeImageAndMove(imageData.get(((int)(Math.random() * 5) + 1) + ".png"));
        }
    }
    
    private void changeImageAndMove(BufferedImage i) {
        switch (direction) {
            case "down":
                i = ImageHandler.flipVertical(i);
                break;
            case "left":
                i = ImageHandler.rotateCW(i, -90);
                break;
            case "right":
                i = ImageHandler.rotateCW(i, 90);
                break;
        }
        changeImage(i);
        switch (direction) {
            case "up":
                changeSize(width(), ImageHandler.calcHeightViaWidth(width(), i));
                move(deathPoint[0] - width() / 2, deathPoint[1] - height());
                break;
            case "down":
                changeSize(width(), ImageHandler.calcHeightViaWidth(width(), i));
                move(deathPoint[0] - width() / 2, deathPoint[1]);
                break;
            case "left":
                move(deathPoint[0] - width(), deathPoint[1] - height() / 2);
                break;
            case "right":
                move(deathPoint[0], deathPoint[1] - height() / 2);
                break;
        }
    }
    
    // Left, right, top, bottom;
    public int[] totalBounds() {
        switch (direction) {
            case "up":
                return new int[] {deathPoint[0] - 100, deathPoint[0] + 100, deathPoint[1] - 800, deathPoint[1]};
            case "down":
                return new int[] {deathPoint[0] - 100, deathPoint[0] + 100, deathPoint[1], deathPoint[1] + 800};
            case "right":
                return new int[] {deathPoint[0], deathPoint[0] + 800, deathPoint[1] - 100, deathPoint[1] + 100};
            case "left":
                return new int[] {deathPoint[0] - 800, deathPoint[0], deathPoint[1] - 100, deathPoint[1] + 100};
        }
        System.out.println("Could not reutrn total bounds of DeathExplosion");
        return null;
    }
    
    public boolean ended() {
        return timer.timer == 0;
    }
}
