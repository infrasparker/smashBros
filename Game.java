import java.awt.*;
import javax.swing.*;
import static java.lang.Runtime.getRuntime;

import src.input.InputHandler;
import src.smash.Smash;
import src.game.GameTemplate;

public class Game extends JFrame {
    private boolean running = true;
    private int fps = 60;
    private final int windowWidth = 1280, windowHeight = 720;
    private Insets insets;
    private InputHandler input;
    private GameTemplate game;
    
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        Game runner = new Game();
        runner.run();
        System.exit(0);
    }
    
    public void run() {
        initialize();
        while (running) {
            long time = System.currentTimeMillis();
            update();
            render();
            long delay = (1000 / fps) - (System.currentTimeMillis() - time);
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch(Exception e) {}
            }
        }
        setVisible(false);
    }
    
    void initialize() {
        setTitle("Game Trial");
        setSize(windowWidth, windowHeight);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        insets = getInsets();
        setSize(insets.left + windowWidth + insets.right,
                insets.top + windowHeight + insets.bottom);
        input = new InputHandler(this);
        game = new Smash(windowWidth, windowHeight, input);
    }
    void update() {
//        if (((Smash)game).phase().equals("battle")) {
//            fps = 5;
//        }
        game.update();
//        System.out.println("Using " + getRuntime().totalMemory() + " out of " + getRuntime().maxMemory());
    }
    
    void render() {
        game.render();
        getGraphics().drawImage(game.backBuffer, insets.left, insets.top, this);
    }
}