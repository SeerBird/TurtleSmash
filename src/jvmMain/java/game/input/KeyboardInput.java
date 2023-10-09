package game.input;

import game.GameHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyboardInput implements KeyListener {
    // Current state of the keyboard

    public synchronized void keyPressed(@NotNull KeyEvent e) {
        InputControl.postKeyPressedEvent(e);
    }

    public synchronized void keyReleased(@NotNull KeyEvent e) {
        InputControl.postKeyReleasedEvent(e);
    }

    public void keyTyped(KeyEvent e) {
        // We'll see
    }
}