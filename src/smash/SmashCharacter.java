package src.smash;

import src.audio.SFXHandler;
import src.genericGameObjects.*;
import src.image.ImageHandler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class SmashCharacter extends Manipulable {
    protected final String name;
    protected byte lives;
    public byte dJUsed;
    protected int percent;
    protected int[][] SCDs;
    protected Hitbubble[] hitbubbles;
    protected double[] knockback;
    protected double kBR;
    protected double groundS, traction, airS, airA, airResistance;
    protected double fallS, fastFallS, fallA;
    protected int jumpH, doubleJumpH, shortHopH, shortHopT;
    protected Map<String, Map> imageData, audioData;
    protected String[] currentStatePath;
    public Map<String, TimeStorage> timers;
    protected String nextTracker;
    public boolean hitUsed;
    public boolean grounded;
    public String direction;
    protected Map<String, Boolean> repressable;
    private boolean winner;
    
    public SmashCharacter(Map<String, Map> iD, Map<String, Map> aD, String n) {
        super((BufferedImage)ImportHandler.navigateData(new String[] {"grounded", "stand", "1.png"}, iD), false);
        imageData = iD;
        audioData = aD;
        lives = 4;
        dJUsed = 0;
        name = n;
        direction = "right";
        knockback = new double[2];
        percent = 0;
        grounded = true;
        SCDs = new int[4][2];
        timers = new HashMap();
        repressable = new HashMap();
        winner = true;
        for (String s : new String[] {"up", "down", "left", "right", "a", "b"}) {
            repressable.put(s, true);
        }
        stand();
    }
    
    public void update() {
        removeTimerIfOver("respawn");
        if (!timers.containsKey("respawn")) {
            removeTimerIfOver("invincible");
            removeTimerIfOver("hitstun");
            removeTimerIfOver("next");
            if (timers.containsKey("frames")) {
                String state = null;
                for (String key : timers.keySet()) {
                    if (!(key.equals("frames") || key.equals("next") ||
                            key.equals("frameCount") || key.contains("_start"))) {
                        state = key;
                    }
                }
                if (timers.get("frames").timer == 0) {
                    Map currentStateImages = (Map)ImportHandler.navigateData(currentStatePath, imageData);
                    if (state != null && !state.equals("jumpSquat")) {
                        if (timers.get(state).timer < currentStateImages.size() / 2) {
                            timers.get(state).timer++;
                            changeImageByData(currentStatePath, timers.get(state).timer);
                        }
                        else {
                            if (state.contains("smash")) {
                                resetVelocity();
                            }
                            timers.remove(state);
                            timers.remove("frames");
                            timers.remove("frameCount");
                        }
                    }
                }
            }
            for (byte i = 0; i < knockback.length; i++) {
                if (knockback[i] > 0) {
                    knockback[i] = Math.max(0, knockback[i] - 1);
                }
                else if (knockback[i] < 0) {
                    knockback[i] = Math.min(0, knockback[i] + 1);
                }
            }
            if (!grounded && (velocity[2] != -fastFallS)) {
                velocity[2] = Math.max(-fallS, velocity[2] - fallA);
            }
            if (!grounded && emptyOrContainsOnly(timers, "next")) {
                fall();
            }
            displace((int)(velocity[1] - velocity[0] + knockback[0]),
                    (int)(velocity[3] - velocity[2] + knockback[1]));
        }
        for (String key : timers.keySet()) {
            if (timers.get(key) != null) {
                timers.get(key).update();
            }
        }
    }
    
    public void keyTrigger(Map<String, Boolean> keys) {
        boolean idle = !(keys.get("left") ^ keys.get("right"));
        
        //<editor-fold defaultstate="collapsed" desc="Jump Logic">
        if (timers.containsKey("frames")) {
            if (timers.containsKey("jumpSquat") && grounded && (timers.get("frames").timer == 0)) {
                jump(jumpH);
                if (keys.get("left") && !keys.get("right") && (velocity[0] < airS)) {
                    velocity[0] = airS;
                }
                else if (keys.get("right") && !keys.get("left") && (velocity[1] < airS)) {
                    velocity[1] = airS;
                }
            }
        }
//</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Short Hop Logic">
        if (timers.containsKey("jumpSquat") && !keys.get("up")) {
            jump(shortHopH);
            if (keys.get("left") && !keys.get("right") && (velocity[0] < airS)) {
                velocity[0] = airS;
            }
            else if (keys.get("right") && !keys.get("left") && (velocity[1] < airS)) {
                velocity[1] = airS;
            }
        }
//</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Double Jump Logic">
        if (!grounded && keys.get("up") && (dJUsed < 1) && repressable.get("up") &&
                (emptyOrContainsOnly(timers, "next") || timers.containsKey("jump"))) {
            doubleJump();
        }
//</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Jump Squat Logic">
        if (emptyOrContainsOnly(timers, "next") && grounded && keys.get("up") && repressable.get("up")) {
            jumpSquat();
        }
//</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Movement Logic">
        boolean applyTraction = true;
        for (String s : timers.keySet()) {
            if (s.contains("smash")) {
                applyTraction = false;
            }
        }
        if (grounded && applyTraction) {
            velocity[0] -= traction;
            velocity[1] -= traction;
            velocity[0] = Math.max(velocity[0], 0);
            velocity[1] = Math.max(velocity[1], 0);
        }
        if ((!grounded && !timers.containsKey("hitstun")) || emptyOrContainsOnly(timers, "next")) {
            String[] x = {"left", "right"};
            for (int i = 0; i < x.length; i++) {
                if (!idle && grounded && keys.get(x[i])) {
                    direction = x[i];
                }
                changeMovement(i, keys.get(x[i]));
            }
        }
//</editor-fold>
        
        if (!grounded && (velocity[2] <= 0) && keys.get("down") && repressable.get("down")) {
            fastFall();
        }

        //<editor-fold defaultstate="collapsed" desc="Attack Logic">
        if ((timers.isEmpty() || timers.containsKey("jump") || timers.containsKey("jumpSquat")) && keys.get("a") && repressable.get("a")) {
            String[] x = new String[] {"left", "right", "up", "down"};
            byte directionsPressed = 0;
            for (String d : x) {
                if (keys.get(d)) {
                    directionsPressed += 1;
                }
            }
            if (grounded) {
                if (directionsPressed == 0) {
                    jab();
                }
                if (directionsPressed == 1) {
                    if (!timers.containsKey("jumpSquat")) {
                        if (keys.get("right")) {
                            fsmash("right");
                        }
                        if (keys.get("left")) {
                            fsmash("left");
                        }
                    }
                    if (keys.get("up")) {
                        usmash();
                    }
                    if (keys.get("down")) {
                        dsmash();
                    }
                }
            }
            else {
                if (directionsPressed == 0) {
                    nair();
                }
                if (directionsPressed == 1) {
                    if ((keys.get("right") && direction.equals("left")) ||
                            (keys.get("left") && direction.equals("right"))) {
                        bair();
                    }
                    if (keys.get("down")) {
                        dair();
                    }
                    if ((keys.get("right") && direction.equals("right")) ||
                            (keys.get("left") && direction.equals("left"))) {
                        fair();
                    }
                    if (keys.get("up")) {
                        uair();
                    }
                }
            }
        }
//</editor-fold>
        
        for (String key : keys.keySet()) {
            if (!(key.equals("left") || key.equals("right"))) {
                if (repressable.get(key)) {
                    idle = idle && !keys.get(key);
                }
            }
        }
        otherTriggers(keys);
        if (idle && grounded && emptyOrContainsOnly(timers, "next")) {
            stand();
        }
        for (String s : repressable.keySet()) {
            if (keys.get(s)) {
                repressable.put(s, false);
            }
            else {
                repressable.put(s, true);
            }
        }
    }
    
    public abstract void otherTriggers(Map<String, Boolean> keys);

    protected void changeMovement(int d, boolean o) {
        if (o) {
            if (grounded) {
                velocity[d] = groundS;
                run();
            }
            else {
                velocity[d] += airA;
                velocity[d] = Math.min(velocity[d], airS);
            }
        }
        else {
            if (!grounded) {
                velocity[d] -= airResistance;
            }
            velocity[d] = Math.max(velocity[d], 0);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="State Changers">
    public int knockback(Hitbubble hB) {
        percent += hB.damage();
        int kB = (int)(hB.baseKB() + hB.kBScaling() * percent / kBR);
        if (kB > 150) {
            SFXHandler.playRandom((Map<String, File>)ImportHandler.navigateData(
                    new String[] {"misc", "hurt"}, audioData));
        }
        int angle = hB.angle();
        double v = Math.sqrt(2 * kB);
        int t = (int)(kB / v * .4);
        for (byte i = 0; i < velocity.length; i++) {
            velocity[i] = 0;
        }
        knockback[0] = v * Math.cos(Math.toRadians(angle));
        knockback[1] = -1 * v * Math.sin(Math.toRadians(angle));
        timers.clear();
        timers.put("hitstun", new TimeStorage(t));
        changeImageByData(new String[] {"airborne", "hitlag"}, 1);
        return kB;
    }
    
    public void stand() {
        changeImageByData(new String[] {"grounded", "stand"}, 1);
    }
    
    public void run() {
        changeImageByData(new String[] {"grounded", "run"}, 1);
        timers.clear();
    }
    
    public void fall() {
        changeImageByData(new String[] {"airborne", "fall"}, 1);
    }
    
    public void jumpSquat() {
        timers.put("jumpSquat", null);
        timers.put("frames", new TimeStorage(shortHopT));
    }
    
    public void jump(int h) {
        timers.remove("jumpSquat");
        velocity[2] = Math.sqrt(2 * fallA * h);
        grounded = false;
        initializeNewState("jump", new String[] {"airborne", "jump"});
        int time = (int)(velocity[2] / fallA + 2.5);
        timers.put("frames", new TimeStorage(time));
        SFXHandler.play((File)ImportHandler.navigateData(new String[] {"airborne", "jump", "jump.wav"}, audioData));
    }
    
    public void doubleJump() {
        dJUsed++;
        velocity[2] = Math.sqrt(2 * fallA * doubleJumpH);
        initializeNewState("jump", new String[] {"airborne", "jump"});
        int time = (int)(velocity[2] / fallA + 2.5);
        timers.put("frames", new TimeStorage(time));
        SFXHandler.play((File)ImportHandler.navigateData(new String[] {"airborne", "jump", "doubleJump.wav"}, audioData));
    }

    public void fastFall() {
        velocity[2] = -fastFallS;
        if (timers.containsKey("jump")) {
            timers.remove("jump");
        }
    }
    
    public abstract void fsmash(String d);
    
    public abstract void usmash();
    
    public abstract void dsmash();
    
    public void bair() {
        initializeNewState("bair", new String[] {"airborne", "bair"});
    }
    
    public void dair() {
        initializeNewState("dair", new String[] {"airborne", "dair"});
    }
    
    public void fair() {
        initializeNewState("fair", new String[] {"airborne", "fair"});
    }
    
    public void uair() {
        initializeNewState("uair", new String[] {"airborne", "uair"});
    }
    
    public void nair() {
        initializeNewState("nair", new String[] {"airborne", "nair"});
    }
    
    public void jab() {
        resetVelocity();
        initializeNewState("jab", new String[] {"grounded", "jab"});
    }

    public void kill() {
	timers.clear();
        lives--;
	percent = 0;
	currentStatePath = null;
	nextTracker = null;
        resetMovement();
        SFXHandler.playRandom((Map)ImportHandler.navigateData(new String[] {"misc", "death"}, audioData));
	timers.put("respawn", new TimeStorage(120));
        changeImageByData(new String[] {"airborne", "fall"}, 1);
        if (lives == 0) {
            winner = false;
        }
    }
    
    public void respawn(int[] rP) {
        timers.clear();
        move(rP[0] - width() / 2, rP[1] - height());
        timers.put("invincible", new TimeStorage(120));
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Image Swapping">
    protected final void initializeNewState(String state, String[] path) {
        timers.clear();
        timers.put(state, new TimeStorage(1, false, true));
        timers.put("frameCount", new TimeStorage(0, false, false));
        nextTracker = state;
        hitUsed = false;
        changeImageByData(path, 1);
    }
    
    protected void changeImageByData(String[] path, int number) {
        Map map = (Map)ImportHandler.navigateData(path, imageData);
        int[] origTopN = SCDs[3];
        currentStatePath = path;
        BufferedImage img = (BufferedImage)map.get(number + ".png");
        Map<String, Object> data = (Map)map.get(number + ".txt");
        int w = (int)data.get("width");
        int[][] newSCDs = (int[][])data.get("SCDs");
        SCDs = new int[4][2];
        for (int i = 0; i < SCDs.length; i++) {
            SCDs[i][0] = newSCDs[i][0];
            SCDs[i][1] = newSCDs[i][1];
        }
        for (int[] SCD : SCDs) {
            SCD[0] = (int)((double)SCD[0] * w / img.getWidth());
            SCD[1] = (int)((double)SCD[1] * w / img.getWidth());
        }
        if (direction.equals("left")) {
            img = ImageHandler.flipHorizontal(img);
            for (int[] SCD : SCDs) {
                SCD[0] = w - SCD[0];
            }
        }
        if (data.containsKey("frames")) {
            int[] frameRange = ((int[])data.get("frames"));
            timers.put("frames", new TimeStorage(frameRange[1] -
                    frameRange[0] + 1));
        }
        if (data.containsKey("next")) {
            int[] nextRange = ((int[])data.get("next"));
            timers.put("next", new TimeStorage(nextRange[1] -
                    nextRange[0] + 1));
        }
        if (data.containsKey("hitbubbles")) {
            ArrayList<Hitbubble> hBs = (ArrayList)data.get("hitbubbles");
            hitbubbles = new Hitbubble[hBs.size()];
            for (int i = 0; i < hitbubbles.length; i++) {
                hitbubbles[i] = new Hitbubble(hBs.get(i));
                hitbubbles[i].resize((double)w / img.getWidth());
                if (direction.equals("left")) {
                    hitbubbles[i].center[0] = w - hitbubbles[i].center[0];
                    hitbubbles[i].flipAngle();
                }
            }
            if (data.containsKey("new")) {
                hitUsed = false;
                Map sfx = (Map)ImportHandler.navigateData(path, audioData);
                if (sfx != null) {
                    SFXHandler.play((File)sfx.get(path[path.length - 1] + ".wav"));
                }
            }
        }
        else {
            hitbubbles = null;
        }
        changeImage(img);
        changeSize(w, ImageHandler.calcHeightViaWidth(w, img));
        int dx = origTopN[0] - SCDs[3][0];
        int dy = origTopN[1] - SCDs[3][1];
        displace(dx, dy);
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Information">
    public Map<String, Map> audioData() {
        return audioData;
    }
    
    public int[][] SCDs() {
        return SCDs;
    }

    public Hitbubble[] hitbubbles() {
        return hitbubbles;
    }

    public String name() {
        return name;
    }

    public Map<String, Map> imageData() {
        return imageData;
    }

    public boolean grounded() {
        return grounded;
    }

    public int percent() {
        return percent;
    }
    
    public byte lives() {
        return lives;
    }
    
    public boolean invincible() {
        return timers.containsKey("invincible");
    }
    
    public boolean respawning() {
        return timers.containsKey("respawn");
    }
    
    public String toString() {
	return name();
    }
//</editor-fold>
    
    public void clearTimers() {
        timers.clear();
    }
    
    public boolean timersContains(String s) {
        return timers.containsKey(s);
    }
    
    public void removeTimerIfOver(String k) {
        if (timers.containsKey(k)) {
            if (timers.get(k).timer == 0) {
                timers.remove(k);
            }
        }
    }
    
    protected static boolean emptyOrContainsOnly(Map map, String key) {
        return (map.containsKey(key) && map.size() == 1) || map.isEmpty();
    }
    
    public void resetVelocity() {
        for (int i = 0; i < velocity.length; i++) {
            velocity[i] = 0;
        }
    }
    
    public void resetKnockback() {
        for (int i = 0; i < knockback.length; i++) {
            knockback[i] = 0;
        }
    }
    
    public void resetMovement() {
        resetVelocity();
        resetKnockback();
    }
    
    public boolean winner() {
        return winner;
    }
}
