package game.input;

import game.GameHandler;
import game.GameState;
import game.output.ui.TurtleMenu;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class InputControl {
    private static final Map<Integer, Boolean> keyPressEvents = new HashMap<>();
    private static final Map<Integer, Boolean> keyReleaseEvents = new HashMap<>();
    private static final Map<Integer, MouseEvent> mousePressEvents = new HashMap<>();
    private static final Map<Integer, MouseEvent> mouseReleaseEvents = new HashMap<>();
    private static MouseEvent mouseMoveEvent;
    public static final ArrayRealVector mousepos = new ArrayRealVector(new Double[]{0.0,0.0});
    static InputInfo input=new InputInfo();
    static GameState state=GameHandler.getState();
    static{
        input.mousepos=mousepos;
        // weird? inputs
        for (int i = 0x10; i <= 0xE3; i++) {
            keyPressEvents.put(i, false);
            keyReleaseEvents.put(i, false);
        }
    }
    public static void handleMenuInput() {
        if (mousePressEvents.get(MouseInput.LEFT) != null) {
            if (TurtleMenu.press(mousepos)) {
                mousePressEvents.put(MouseInput.LEFT, null);
            }
        }
        if (mouseReleaseEvents.get(MouseInput.LEFT) != null) {
            if (TurtleMenu.release()) {
                mouseReleaseEvents.put(MouseInput.LEFT, null);
            }
        }
        if (keyPressEvents.get(KeyEvent.VK_SPACE)) {
            GameHandler.debug = true;
            keyPressEvents.put(KeyEvent.VK_SPACE, false);
        }
        if (keyReleaseEvents.get(KeyEvent.VK_SPACE)) {
            GameHandler.debug = false;
            keyReleaseEvents.put(KeyEvent.VK_SPACE, false);
        }
        if (keyPressEvents.get(KeyEvent.VK_ESCAPE)) {
            if (state == GameState.playClient) {
                GameHandler.disconnectTCPClient();
            } else if (state == GameState.lobby) {
                GameHandler.lobbyToDiscover();
            } else if (state == GameState.discover) {
                GameHandler.discoverToMain();
            } else if (state == GameState.playServer) {
                GameHandler.playToHost();
            } else if (state == GameState.host) {
                GameHandler.hostToMain();
            }
            keyPressEvents.put(KeyEvent.VK_ESCAPE, false);
        }
        if (keyReleaseEvents.get(KeyEvent.VK_A)) {
            getGameInput();
            keyPressEvents.put(KeyEvent.VK_A, false);
            keyReleaseEvents.put(KeyEvent.VK_A, false);
        }
    }

    public static void getGameInput() {
        input.reset();
        if (mousePressEvents.get(MouseInput.LEFT) != null) {
            input.teleport();
        }
        if (mouseReleaseEvents.get(MouseInput.LEFT) != null) {
            mousePressEvents.put(MouseInput.LEFT, null);
            mouseReleaseEvents.put(MouseInput.LEFT, null);
        }
        if (keyPressEvents.get(KeyEvent.VK_C)) {
            input.create();
            keyPressEvents.put(KeyEvent.VK_C, false);
        }
        if (mouseReleaseEvents.get(MouseInput.RIGHT) != null) {
            input.webFling();
            mousePressEvents.put(MouseInput.RIGHT, null);
            mouseReleaseEvents.put(MouseInput.RIGHT, null);
        }
    }
    public static void postKeyPressedEvent(@NotNull KeyEvent e) {
        keyPressEvents.put(e.getKeyCode(), true);
    }

    public static ArrayRealVector getMousepos() {
        return mousepos;
    }

    public static void postKeyReleasedEvent(@NotNull KeyEvent e) {
        keyReleaseEvents.put(e.getKeyCode(), true);
    }

    public static void postMousePressEvent(MouseEvent e) {
        mousePressEvents.put(e.getButton(), e);
    }

    public static void postMouseReleaseEvent(MouseEvent e) {
        mouseReleaseEvents.put(e.getButton(), e);
    }

    public static void postMouseMoveEvent(@NotNull MouseEvent e) {
        mouseMoveEvent = e;
        mousepos.setEntry(0, e.getPoint().x);
        mousepos.setEntry(1, e.getPoint().y);
    }

    public static InputInfo getInput() {
        return input;
    }
}
