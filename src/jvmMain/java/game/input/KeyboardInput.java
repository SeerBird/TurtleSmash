package game.input;

import game.Config;
import game.EventManager;
import game.output.GameWindow;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Map;

public class KeyboardInput implements KeyListener {
    // Current state of the keyboard
    EventManager handler;

    public KeyboardInput(EventManager handler) {
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