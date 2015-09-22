package src.characters;

import java.io.File;
import java.util.Map;

import src.audio.SFXHandler;
import src.genericGameObjects.TimeStorage;
import src.smash.ImportHandler;
import src.smash.SmashCharacter;

public class Fox extends SmashCharacter {
    private boolean firstJab3;
    
    public Fox(Map<String, Map> iD, Map<String, Map> aD) {
        super(iD, aD, "fox");
        kBR = 5;
        groundS = 15;
        traction = 3;
        airS = 10;
        airA = .8;
        airResistance = .4;
        fallS = 20;
        fallA = 3;
        fastFallS = 22;
        jumpH = 200;
        doubleJumpH = 150;
        shortHopH = 60;
        shortHopT = 4;
        firstJab3 = true;
    }
    
    public void fsmash(String d) {
        direction = d;
        resetVelocity();
        switch (d) {
            case "left":
                velocity[0] = 3;
                break;
            case "right":
                velocity[1] = 3;
                break;
        }
        SFXHandler.playRandom((Map)ImportHandler.navigateData(new String[] {"misc", "smash"}, audioData));
        initializeNewState("fsmash", new String[] {"grounded", "fsmash"});
    }
    
    public void usmash() {
        velocity[0] = Math.min(1, velocity[0]);
        velocity[1] = Math.min(1, velocity[1]);
        SFXHandler.playRandom((Map)ImportHandler.navigateData(new String[] {"misc", "smash"}, audioData));
        initializeNewState("usmash", new String[] {"grounded", "usmash"});
    }
    
    public void dsmash() {
        resetVelocity();
        SFXHandler.playRandom((Map)ImportHandler.navigateData(new String[] {"misc", "smash"}, audioData));
        initializeNewState("fsmash", new String[] {"grounded", "dsmash"});
    }
    
    public void otherTriggers(Map<String, Boolean> keys) {
        if (timers.containsKey("next")) {
            if (nextTracker.equals("jab") && keys.get("a") && (timers.get("next").timer > 0) && repressable.get("a")) {
                timers.put("jab2_start", new TimeStorage(6, true));
            }
            if (nextTracker.equals("jab2") && keys.get("a") && (timers.get("next").timer > 0) && repressable.get("a")) {
                timers.put("jab3_start", new TimeStorage(8, true));
                firstJab3 = true;
            }
            if (nextTracker.equals("jab3") && keys.get("a") && (timers.get("next").timer > 0) && repressable.get("a")) {
                timers.put("jab3_start", new TimeStorage(33, true));
                firstJab3 = false;
            }
        }
        String bufferedState = null;
        for (String s : timers.keySet()) {
            if (s.contains("_start")) {
                bufferedState = s;
            }
        }
        if (bufferedState != null) {
            String s = bufferedState.substring(0, bufferedState.indexOf("_start"));
            if (timers.containsKey("frameCount")) {
                if (timers.get(bufferedState).time0 <= timers.get("frameCount").timer) {
                    if (s.equals("jab2")) {
                        jab2();
                    }
                    else if (s.equals("jab3")) {
                        jab3();
                    }
                }
            }
            else {
                initializeNewState(s, new String[] {"grounded", s});
            }
        }
    }
    
    public void jab2() {
        initializeNewState("jab2", new String[] {"grounded", "jab2"});
    }
    
    public void jab3() {
        if (firstJab3) {
            SFXHandler.play((File)ImportHandler.navigateData(new String[]
                    {"grounded", "jab3", "voice01.wav"}, audioData));
        }
        initializeNewState("jab3", new String[] {"grounded", "jab3"});
    }
}
