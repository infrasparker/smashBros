package src.characters;

import java.util.Map;

import src.smash.SmashCharacter;
/**
 *
 * @author Devin
 */
public class Falco extends SmashCharacter {
    public Falco(Map<String, Map> iD, Map<String, Map> aD) {
        super(iD, aD, "falco");
        lives = 4;
        kBR = 6;
        groundS = 8;
        traction = 3;
        airS = 8;
        airA = .8;
        airResistance = .4;
        fallS = 20;
        fallA = 5;
        fastFallS = 22;
        jumpH = 250;
        doubleJumpH = 200;
        shortHopH = 60;
        shortHopT = 4;
    }
    
    public void update() {
        
    }
    
    public void otherTriggers(Map<String, Boolean> keys) {
        
    }
}
