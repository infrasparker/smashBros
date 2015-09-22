package src.genericGameObjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class SongStorage {
    public final String directory;
    private AudioStream currentSong;
    
    public SongStorage(String d) {
        directory = d;
    }
    
    public void play(String s) {
        play(new File(directory + "\\" + s));
    }
    
    public void play(File f) {
        try {
            currentSong = new AudioStream(new FileInputStream(f));
            AudioPlayer.player.start(currentSong);
        } catch(Exception e) {
            System.out.println("Could not play file: " + f.getPath());
        }
    }
    
    public void playRandom() {
        playRandom("");
    }
    
    public void playRandom(String s) {
        if (!s.equals("")) {
            s = "\\" + s;
        }
        File[] files = new File(directory + s).listFiles();
        ArrayList<File> audioFiles = new ArrayList();
        for (File f : files) {
            if (f.isFile() && !f.getName().equals("Thumbs.db")) {
                audioFiles.add(f);
            }
        }
        int r = (int)(Math.random() * audioFiles.size());
        play(audioFiles.get(r));
    }
    
    public void stop() {
        AudioPlayer.player.stop(currentSong);
    }
}
