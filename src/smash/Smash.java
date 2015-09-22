package src.smash;

import src.characters.Fox;
import src.audio.SFXHandler;
// import src.characters.Falco;
import src.collision.CollisionHandler;
import src.game.GameTemplate;
import src.genericGameObjects.SongStorage;
import src.genericGameObjects.Sprite;
import src.genericGameObjects.TimeStorage;
import src.genericGameObjects.Updatable;
import src.image.ImageHandler;
import src.input.InputHandler;
import src.stages.FinalDestination;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

public class Smash extends GameTemplate {
    private Player player1, player2;
    private final ArrayList<Player> players;
    private final ArrayList<Sprite> entities;
    private final ArrayList<Updatable> updatables;
    private final ArrayList<Stage> stages;
    private final ArrayList<CharacterSelectorPortrait> characterSelectorPortraits;
    private LinkedHashMap<String, Map> portraits;
    private Map<String, BufferedImage> menuImages;
    private Map<String, Map> deathImages;
    private final SongStorage music;
    private MenuBackground mBG;
    private boolean debugMode;
    private Camera camera;
    private String phase;
    private Stage stage;
    private int hitlag;
    private Map<String, TimeStorage> timers;
    
    public Smash(int wW, int wH, InputHandler in) {
        entities = new ArrayList();
        updatables = new ArrayList();
        characterSelectorPortraits = new ArrayList();
        stages = new ArrayList();
        portraits = ImportHandler.generateSortedImageData("smash\\images\\portraits");
        menuImages = ImportHandler.generateImageData("smash\\images\\menu");
        deathImages = ImportHandler.generateImageData("smash\\images\\deaths");
        windowWidth = wW;
        windowHeight = wH;
        input = in;
        debugMode = false;
        backBuffer = new BufferedImage(windowWidth, windowHeight,
                BufferedImage.TYPE_INT_ARGB);
        players = new ArrayList();
        player1 = new Player(1, input, windowWidth, windowHeight);
        player2 = new Player(2, input, windowWidth, windowHeight);
        music = new SongStorage("smash\\audio\\songs");
        mBG = new MenuBackground(windowWidth, windowHeight, menuImages.get("BG.png"));
        phase = "select";
        hitlag = 0;
        timers = new HashMap();
        InitializeCharacterSelectScreen(6);
    }

    private void InitializeCharacterSelectScreen(int maxRow) {
        entities.add(mBG);
        updatables.add(mBG);
        stages.add(new FinalDestination());
        CharacterSelectorPortrait[] csps = new CharacterSelectorPortrait[portraits.size()];
        int c = 1;
        for (String n : portraits.keySet()) {
            BufferedImage i = (BufferedImage)(portraits.get(n).get("CSP.png"));
            int x = (c % maxRow - 1) * (i.getWidth());
            int y = ((c - 1) / maxRow) * i.getHeight();
            csps[c - 1] = new CharacterSelectorPortrait(x, y, i, n);
            c++;
        }
        for (byte i = 0; i < csps.length; i++) {
            csps[i].displace(windowWidth / 2 - (maxRow / 2) *
                    csps[i].image().getWidth(), windowHeight / 3);
            if (i >= (csps.length - csps.length % maxRow)) {
                csps[i].displace((maxRow - csps.length % maxRow) *
                        csps[i].image().getWidth() / 2 , 0);
		if ((csps.length % maxRow) % 2 == 0) {
		    csps[i].displace(((csps.length % maxRow) / 2 - 1) * -10 - 5 + (i % maxRow) * 10, 0);
                }
                else {
                    csps[i].displace(((csps.length % maxRow) / 2) * -10 + (i % maxRow) * 10, 0);
                }
            }
            characterSelectorPortraits.add(csps[i]);
            entities.add(csps[i]);
        }
        players.add(player1);
        players.add(player2);
        updatables.addAll(players);
        for (Player p : players) {
            p.selector.csps = characterSelectorPortraits;
            entities.add(p.inUse);
        }
        music.playRandom("menu");
        SFXHandler.play("smash\\audio\\sfx\\menu\\misc\\chooseChar.wav");
    }
    
    private void initializeBattle() {
        entities.clear();
        updatables.clear();
        music.stop();
        SFXHandler.play("smash\\audio\\sfx\\menu\\misc\\start.wav");
        phase = "battle";
        stage = stages.get((int)(Math.random() * stages.size()));
        camera = new Camera(windowWidth, windowHeight, 500, stage);
        for (Player p : players) {
            Map<String, Map> imageData = ImportHandler.generateImageData(
                    "smash\\images\\characters\\" + p.selector.currentChar(), .25);
            Map<String, Map> audioData = ImportHandler.generateAudioData(
                    "smash\\audio\\sfx\\characters\\" + p.selector.currentChar());
            switch(p.selector.currentChar()) {
                case "fox":
                    p.character = new Fox(imageData, audioData);
                    break;
                case "falco":
                    // p.character = new Falco(imageData, audioData);
                    break;
            }
            p.inUse = p.character;
            int x = stage.spawnPoints()[p.number() - 1][0] - p.character.SCDs()[3][0];
            int y = stage.spawnPoints()[p.number() - 1][1] - p.character.SCDs()[3][1];
            if (x > stage.width() / 2) {
                p.character.direction = "left";
            }
            p.character.move(x, y);
        }
        for (Player p : players) {
            camera.addToAll(p.character);
        }
        camera.addEntities(stage.platforms());
        updatables.addAll(players);
        music.playRandom(stage.name());
    }
    
    private void initializeVictoryScreen() {
        phase = "end";
        timers.remove("battleOver");
        timers.remove("frameSkip");
        SFXHandler.play("smash\\audio\\sfx\\menu\\misc\\winner.wav");
        timers.put("victoryName", new TimeStorage(120));
    }

    public void update() {
        detectDebugPresses();
        if (hitlag == 0) {
            boolean update = true;
            if (timers.containsKey("frameSkip")) {
                if (timers.get("frameSkip").timer > 0) {
                    update = false;
                }
                else {
                    timers.get("frameSkip").reset();
                }
            }
            if (update) {
                for (Updatable u : updatables) {
                    u.update();
                }
            }
            if (phase.equals("select")) {
                detectMenuPresses();
                detectCSBoundCollisions();
                detectCSInternalCollisions();
            }
            else if (phase.equals("battle")) {
                removeEndedParticles();
                detectStageCollisions();
                detectHitbubbleCollisions();
                detectDeathCondition();
                detectWinCondition();
            }
            else if (phase.equals("end")) {
                if (timers.containsKey("victoryName")) {
                    if (timers.get("victoryName").timer == 0) {
                        timers.remove("victoryName");
                        SmashCharacter winner = null;
                        for (Player p : players) {
                            if (p.character.winner()) {
                                winner = p.character;
                            }
                        }
                        SFXHandler.play("smash\\audio\\sfx\\menu\\names\\" + winner.name() + ".wav");
                    }
                }
            }
        }
        else {
            hitlag--;
        }
        for (TimeStorage t : timers.values()) {
            t.update();
        }
    }
    
    public void detectDebugPresses() {
        if (input.keyDown(KeyEvent.VK_T)) {
            debugMode = !debugMode;
        }
    }
    
    private void detectMenuPresses() {
        boolean allLocked = true;
        for (Player p : players) {
            if (!p.selector.locked()) {
                allLocked = false;
            }
        }
        if (input.keyDown(KeyEvent.VK_SPACE) && allLocked) {
            initializeBattle();
        }
    }
    
    private void detectCSBoundCollisions() {
        for (Player p : players) {
            Selector s = p.selector;
            if (s.left() < 0) {
                s.move(0, s.top());
            }
            if (s.right() > windowWidth) {
                s.move(windowWidth - s.width(), s.top());
            }
            if (s.top() < 0) {
                s.move(s.left(), 0);
            }
            if (s.bottom() > windowHeight) {
                s.move(s.left(), windowHeight - s.height());
            }
        }
    }
    
    private void detectCSInternalCollisions() {
        for (Player p : players) {
            Selector s = p.selector;
            if (s.locked()) {
                CharacterSelectorPortrait csp = null;
                for (CharacterSelectorPortrait c : characterSelectorPortraits) {
                    if (CollisionHandler.detectPointInRectangular(s.center(), c)) {
                        csp = c;
                    }
                }
                int mag = 2;
                if (s.left() < csp.left() - 5) {
                    s.displace(mag, 0);
                }
                if (s.right() > csp.right() + 5) {
                    s.displace(-mag, 0);
                }
                if (s.top() < csp.top() - 5) {
                    s.displace(0, mag);
                }
                if (s.bottom() > csp.bottom() + 5) {
                    s.displace(0, -mag);
                }
            }
        }
	ArrayList<Player[]> collisionPairs = selectorCollisions(new ArrayList(players));
        for (Player[] pair : collisionPairs) {
	    Selector p1s = pair[0].selector, p2s = pair[1].selector;
	    if (p1s.locked() && p2s.locked()) {
		double angle;
                int mag = 2;
                if (p2s.center()[0] != p1s.center()[0]) {
                    angle = Math.toDegrees(Math.atan((p2s.center()[1] - p1s.center()[1]) /
			    (p2s.center()[0] - p1s.center()[0])));
		}
		else {
		    angle = 90;
		}
                // x1, y1, x2, y2.
                int[] d = new int[] {mag, mag, mag, mag};
                for (int i = 0; i <= 1; i++) {
                    if (p1s.center()[i] < p2s.center()[i]) {
                        d[i] *= -1;
                    }
                    else {
                        d[i + 2] *= -1;
                    }
                }
		p1s.displace((int)(d[0] * Math.cos(angle)), (int)(d[1] * Math.sin(angle)));
		p2s.displace((int)(d[2] * Math.cos(angle)), (int)(d[3] * Math.sin(angle)));
            }
	}
    }

    private ArrayList<Player[]> selectorCollisions(ArrayList<Player> testingPlayers) {
        ArrayList<Player[]> collisionPairs = new ArrayList();
        while (testingPlayers.size() > 1) {
            Player p = testingPlayers.remove(0);
            for (Player pOther : testingPlayers) {
                if (CollisionHandler.detectCircular(p.selector, pOther.selector)) {
                    collisionPairs.add(new Player[] {p, pOther});
                }
            }
        }
        return collisionPairs;
    }

    private void removeEndedParticles() {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i) instanceof DeathExplosion) {
                if (((DeathExplosion)entities.get(i)).ended()) {
                    Sprite x = entities.remove(i);
                    camera.removeFromAll(x);
                    i--;
                }
            }
        }
    }
    
    private void detectStageCollisions() {
        for (Player p : players) {
            for (Sprite platform : stage.solids()) {
                int[] botSCD = {p.character.SCDs()[3][0] + p.character.left(),
                    p.character.SCDs()[3][1] + p.character.top()};
                if (CollisionHandler.detectPointInRectangular(botSCD, platform)) {
                    p.character.move(p.character.left(), platform.top() -
                            p.character.SCDs()[3][1]);
                    p.character.setVelocity("up", 0);
                    p.character.dJUsed = 0;
                    if (!p.character.grounded) {
                        p.character.clearTimers();
                        p.character.grounded = true;
                        p.character.stand();
                    }
                }
                else {
                    p.character.grounded = false;
                }
            }
        }
    }
    
    private void detectHitbubbleCollisions() {
        for (Player p : players) {
            if (!p.character.hitUsed) {
                ArrayList<Player> otherPlayers = new ArrayList(players);
                otherPlayers.remove(p);
                for (Player p2 : otherPlayers) {
                    if (p.character.hitbubbles() != null) {
                        ArrayList<Hitbubble> hits = new ArrayList();
                        for (Hitbubble hB : p.character.hitbubbles()) {
                            int cx = hB.center()[0] + p.character.left();
                            int cy = hB.center()[1] + p.character.top();
                            if (CollisionHandler.detectRectangularCircular(
                                    p2.character, cx, cy, hB.radius())) {
                                hits.add(hB);
                            }
                        }
                        if (hits.size() > 0) {
                            Hitbubble hB = hits.get(0);
                            for (Hitbubble hit : hits) {
                                if (hit.priority() > hB.priority()) {
                                    hB = hit;
                                }
                            }
                            SFXHandler.play("smash\\audio\\sfx\\battle\\hits\\" + hB.sfx() + ".wav");
                            int kB = p2.character.knockback(hB);
                            hitlag = (int)(kB * 7 / 1000d) + 1;
                            p.character.hitUsed = true;
                        }
                    }
                }
            }
        }
    }

    private void detectDeathCondition() {
	for (Player p : players) {
            if (p.character.timersContains("respawn")) {
                if ((p.character.timers.get("respawn").timer == 0) && (p.character.lives() > 0)) {
                    respawnCharacter(p.character);
                }
            }
            else {
                if (p.character.bottom() <= 0) {
                    killCharacter(p, "top");
                }
                else if (p.character.top() >= stage.height()) {
                    killCharacter(p, "bottom");
                }
                else if (p.character.right() <= 0) {
                    killCharacter(p, "left");
                }
                else if (p.character.left() >= stage.width()) {
                    killCharacter(p, "right");
                }
            }
	}
    }

    private void killCharacter(Player p, String d) {
	p.character.kill();
        int[] deathPoint = new int[2];
        switch (d) {
            case "top":
                deathPoint[0] = p.character.center()[0];
                deathPoint[1] = p.character.bottom();
                d = "down";
                break;
            case "bottom":
                deathPoint[0] = p.character.center()[0];
                deathPoint[1] = p.character.top();
                d = "up";
                break;
            case "left":
                deathPoint[0] = p.character.right();
                deathPoint[1] = p.character.center()[1];
                d = "right";
                break;
            case "right":
                deathPoint[0] = p.character.left();
                deathPoint[1] = p.character.center()[1];
                d = "left";
                break;
        }
        DeathExplosion e = new DeathExplosion(deathPoint[0], deathPoint[1], (Map)ImportHandler.navigateData(
                new String[] {"player" + p.number()}, deathImages), d);
        camera.removeFromAll(p.character);
        entities.add(e);
        updatables.add(e);
        camera.addToAll(e);
    }
    
    private void respawnCharacter(SmashCharacter c) {
        int[] respawnPoint = stage.respawnPoints()[(int)(Math.random() * stage.respawnPoints().length)];
        c.respawn(respawnPoint);
        camera.addToAll(c);
    }
    
    private void detectWinCondition() {
        if (timers.containsKey("battleOver")) {
            if (timers.get("battleOver").timer == 0) {
                initializeVictoryScreen();
            }
        }
        else {
            ArrayList<Player> losers = new ArrayList();
            for (Player p : players) {
                if (!p.character.winner()) {
                    losers.add(p);
                }
            }
            if (players.size() - losers.size() == 1) {
                music.stop();
                timers.put("battleOver", new TimeStorage(120));
                timers.put("frameSkip", new TimeStorage(5));
                SFXHandler.play("smash\\audio\\sfx\\battle\\misc\\game.wav");
            }
        }
    }

    public void render() {
        Graphics2D g = backBuffer.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, windowWidth, windowHeight);
        for (Sprite s : entities) {
                g.drawImage(s.image(), s.left(), s.top(), null);
        }
        switch (phase) {
            case "select":
                drawMenuObjects(g);
                break;
            case "battle":
                g.drawImage(camera.capture(), 0, 0, null);
                g.setColor(Color.red);
                g.setFont(new Font("SERIF", Font.BOLD, 40));
                for (Player p : players) {
                    SmashCharacter ch = p.character;
                    for (byte c = 0; c < ch.lives(); c++) {
                        BufferedImage stock = (BufferedImage)portraits.get(
                                ch.name()).get("stock.png");
                        g.drawString(ch.percent() + "%", (int)(windowWidth * p.number() /
                                (players.size() + 1.0) - ch.lives() * stock.getWidth() / 2.0 - 70),
                                windowHeight * 9 / 10);
                        g.drawImage(stock, (int)(windowWidth * p.number() / (players.size() + 1.0) - ch.lives() *
                                stock.getWidth() / 2.0 + c * stock.getWidth()), windowHeight * 9 / 10, null);
                    }
                }
                break;
            case "end":
                BufferedImage bG = menuImages.get("endBG.png");
                g.drawImage(bG, (windowWidth - bG.getWidth()) / 2, (windowHeight - bG.getHeight()) / 2, null);
                for (byte i = 0; i < players.size(); i++) {
                    BufferedImage image = null;
                    if (players.get(i).character.winner()) {
                        image = (BufferedImage)players.get(i).character.imageData().get("misc").get("victory.png");
                        image = ImageHandler.resize(image, 150);
                    }
                    else {
                        image = (BufferedImage)players.get(i).character.imageData().get("misc").get("defeat.png");
                        image = ImageHandler.resize(image, 75);
                    }
                    g.drawImage(image, image.getWidth() / 2 + 50 + (windowWidth - 100) * (i + 1) / (players.size() + 1),
                            windowHeight / 3, null);
                }
                break;
        }
    }
    
    private void drawMenuObjects(Graphics2D g) {
        for (byte i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p.selector.currentChar() != null) {
                BufferedImage portrait = (BufferedImage)(portraits.get(
                        p.selector.currentChar()).get("P.png"));
                BufferedImage portraitTag = (BufferedImage)(portraits.get(
                        p.selector.currentChar()).get("Ptag.png"));
                AlphaComposite a;
                if (p.selector.locked()) {
                    a = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
                    g.drawImage(portraitTag, (i + 1) * windowWidth / 5 - portraitTag.getWidth() / 2,
                        windowHeight - portraitTag.getHeight(), null);
                }
                else {
                    a = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
                }
                g.setComposite(a);
                g.drawImage(portrait, (i + 1) * windowWidth / 5 - portrait.getWidth() / 2,
                        windowHeight - portraitTag.getHeight() - portrait.getHeight(), null);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            }
        }
        boolean allLocked = true;
        for (Player p : players) {
            if (p.selector.locked() == false) {
                allLocked = false;
            }
        }
        if (allLocked) {
            BufferedImage r = ImageHandler.resize(menuImages.get("ready.png"),
                    (double)windowWidth / menuImages.get("ready.png").getWidth());
            BufferedImage rBG = ImageHandler.resize(menuImages.get("readyBG.png"), 
                    (double)windowWidth / menuImages.get("readyBG.png").getWidth());
            g.drawImage(rBG, 0, windowHeight - 186 - rBG.getHeight(), null);
            g.drawImage(r, 0, windowHeight - 188 - (rBG.getHeight() + r.getHeight()) / 2, null);
        }
        if (debugMode) {
            g.setColor(Color.green);
            g.drawLine(windowWidth / 2, 0, windowWidth / 2, windowHeight);
        }
        g.dispose();
    }

    public String phase() {
        return phase;
    }
}