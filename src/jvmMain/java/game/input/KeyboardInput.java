package game.input;

import game.GameHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyboardInput implements KeyListener {
    // Current state of the keyboard
    GameHandler handler;
    private static final Map<Integer, Boolean> keyPressedEvents = new HashMap<>();
    private static final Map<Integer, Boolean> keyReleasedEvents = new HashMap<>();
    public KeyboardInput(GameHandler handler) {
        this.handler = handler;
    }

    public synchronized void keyPressed(@NotNull KeyEvent e) {
        handler.postKeyPressedEvent(e);
    }

    public synchronized void keyReleased(@NotNull KeyEvent e) {
        handler.postKeyReleasedEvent(e);
    }

    public void keyTyped(KeyEvent e) {
        // We'll see
    }
}