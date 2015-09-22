package src.input;

import java.awt.Component;
import java.awt.event.*;
import java.util.*;

public class InputHandler implements KeyListener{
    private boolean[] keys = new boolean[256];
    KeyListener k;
    
    public InputHandler(Component c) {
        c.addKeyListener(this);
    }

    public InputHandler() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean keyDown(int keyCode) {
        if (keyCode > 0 && keyCode < 256) {
            return keys[keyCode];
        } else {
            return false;
        }
    }
    
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = true;
        }
    }
    
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = false;
        }
    }
    
    public void keyTyped(KeyEvent e) {}
}
