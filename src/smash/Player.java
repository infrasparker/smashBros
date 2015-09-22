package src.smash;

import src.genericGameObjects.*;
import src.input.ControlHandler;
import src.input.InputHandler;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Player implements Updatable {
    public SmashCharacter character;
    public Selector selector;
    public Manipulable inUse;
    public InputHandler input;
    private Map<String, Integer> controls;
    private final int number;
    
    public Player(int n, InputHandler i, int wW, int wH) {
        number = n;
        input = i;
        createControls();
        int x, y = wH * 3 / 4;
        if (n == 1) {
            x = wW / 3;
        }
        else {
            x = wW * 2 / 3;
        }
        selector = new Selector(x, y, ImportHandler.readImage("smash\\images\\" +
                "players\\ps" + number + ".png"));
        inUse = selector;
    }
    
    public void update() {
        Map<String, Boolean> keys = new HashMap();
        for (String s : controls.keySet()) {
            keys.put(s, input.keyDown(controls.get(s)));
        }
        inUse.keyTrigger(keys);
        inUse.update();
    }
    
    private void createControls() {
        String[] keys = {"left", "right", "up", "down", "a", "b"};
        int[] hotkeys;
        if (number == 1) {
            hotkeys = new int[] {KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W,
                KeyEvent.VK_S, KeyEvent.VK_C, KeyEvent.VK_V};
        }
        else {
            hotkeys = new int[] {KeyEvent.VK_L, KeyEvent.VK_QUOTE, KeyEvent.VK_P,
                KeyEvent.VK_SEMICOLON, KeyEvent.VK_COMMA, KeyEvent.VK_M};
        }
        controls = ControlHandler.createControls(keys, hotkeys);
    }
    
    public int number() {
        return number;
    }
    
    public String toString() {
        return "Player " + number;
    }
}
