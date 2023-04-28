package game.input;

import game.Config;
import game.output.GameWindow;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Map;

public class KeyboardInput implements KeyListener {

    public enum KeyState {
        RELEASED, // Not down
        PRESSED,  // Down, but not the first time
        ONCE      // Down for the first time
    }

    // Current state of the keyboard
    private boolean[] currentKeys = null;
    private Map<Integer, ArrayList<Integer>> keyEvents; // list 0 is released, list 1 is pressed
    GameWindow win;

    public KeyboardInput(GameWindow win) {
        this.win = win;
        currentKeys = new boolean[Config.KEY_COUNT];
    }

    public GameWindow getWindow() {
        return win;
    }

    public boolean keyDown(int keyCode) {
        return currentKeys[keyCode];
    }

    public synchronized void keyPressed(@NotNull KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < Config.KEY_COUNT) {
            currentKeys[keyCode] = true;
        }
        getWindow().getHandler().postKeyPressedEvent(e);
    }

    public synchronized void keyReleased(@NotNull KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < Config.KEY_COUNT) {
            currentKeys[keyCode] = false;
        }
        getWindow().getHandler().postKeyReleasedEvent(e);
    }

    public void keyTyped(KeyEvent e) {
        // We'll see
    }
}