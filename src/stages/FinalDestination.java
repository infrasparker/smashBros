package src.stages;

import src.genericGameObjects.SongStorage;
import src.genericGameObjects.Sprite;
import src.image.ImageHandler;
import src.smash.ImportHandler;
import src.smash.Stage;

public class FinalDestination extends Stage {
    public FinalDestination() {
        super(1600, 1000, "finalDestination");
    }
    
    public void generateStage() {
        Sprite main = new Sprite(width / 2 - 375, 600, 750, ImportHandler.readImage(
                "smash\\images\\stages\\finalDestination\\main.png"), false);
        solids.add(main);
        platforms.addAll(solids);
        for (int i = 0; i < spawnPoints.length; i++) {
            spawnPoints[i] = new int[] {(main.right() - main.left() - 40) * i +
                    20 + main.left(), main.top()};
        }
        for (int n = 0; n < respawnPoints.length; n++) {
            respawnPoints[n][0] = main.left() + main.width() * (n + 2) / 6;
            respawnPoints[n][1] = main.top() / 2;
        }
        background = ImportHandler.readImage("smash\\images\\stages\\"
                + "finalDestination\\bG.png");
    }
}
