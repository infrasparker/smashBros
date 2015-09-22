package src.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author Devin
 */
public class SFXHandler {
    public static void playRandom(String s) {
        playRandom(new File(s));
    }
    
    public static void playRandom(File f) {
        playRandom(f.listFiles());
    }
    
    public static void playRandom(File[] f) {
        play(f[(int)(Math.random() * f.length)]);
    }
    
    public static void playRandom(Map<String, File> map) {
        ArrayList<File> files = new ArrayList();
        for (File f : map.values()) {
            files.add(f);
        }
        play(files.get((int)(Math.random() * files.size())));
    }
    
    public static void play(String s) {
        play(new File(s));
    }
    
    public static void play(File f) {
        try {
            Clip c = AudioSystem.getClip();
            c.open(AudioSystem.getAudioInputStream(f));
            c.start();
        } catch (Exception e) {
            System.out.println("Could not play file: " + f.getName());
        }
        
    }
}
