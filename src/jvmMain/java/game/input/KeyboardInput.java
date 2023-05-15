package game.input;

import game.Config;
import game.output.GameWindow;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Map;

public class KeyboardInput implements KeyListener {
    // Current state of the keyboard
    GameWindow win;

    public KeyboardInput(GameWindow win) {
        this.win = win;
    }

    public GameWindow getWindow() {
        return win;
    }
    public synchronized void keyPressed(@NotNull KeyEvent e) {
        getWindow().getHandler().postKeyPressedEvent(e);
    }

    public synchronized void keyReleased(@NotNull KeyEvent e) {
        getWindow().getHandler().postKeyReleasedEvent(e);
    }

    public void keyTyped(KeyEvent e) {
        // We'll see
    }
}