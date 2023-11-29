package game.input;

import game.GameHandler;
import game.GameState;
import game.output.ui.TurtleMenu;
import game.output.ui.rectangles.Textbox;
import game.util.DevConfig;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import static game.GameState.playClient;
import static game.GameState.playServer;
import static game.input.InputControl.Mousebutton.Left;
import static game.input.InputControl.Mousebutton.Right;
import static java.awt.event.KeyEvent.*;

public class InputControl extends MouseAdapter implements KeyListener {
    //region Events
    private static final Map<Integer, Boolean> keyPressEvents = new HashMap<>();
    private static final Map<Integer, Boolean> keyReleaseEvents = new HashMap<>();
    private static final Map<Mousebutton, MouseEvent> mousePressEvents = new HashMap<>();
    private static final Map<Mousebutton, MouseEvent> mouseReleaseEvents = new HashMap<>();
    private static MouseEvent mouseMoveEvent;

    //endregion
    //region MouseListener methods
    @Override
    public void mousePressed(MouseEvent e) {
        mousePressEvents.put(getButton(e.getButton()), e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseReleaseEvents.put(getButton(e.getButton()), e);
    }

    @Override
    public void mouseMoved(@NotNull MouseEvent e) {
        mouseMoveEvent = e;
        mousepos.setEntry(0, e.getPoint().x);
        mousepos.setEntry(1, e.getPoint().y);
    }

    @Override
    public void mouseDragged(@NotNull MouseEvent e) {
        mouseMoveEvent = e;
        mousepos.setEntry(0, e.getPoint().x);
        mousepos.setEntry(1, e.getPoint().y);
    }

    //endregion
    //region KeyListener methods
    @Override
    public void keyPressed(@NotNull KeyEvent e) {
        keyPressEvents.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(@NotNull KeyEvent e) {
        keyReleaseEvents.put(e.getKeyCode(), true);
    }

    //region (not used)
    @Override
    public void keyTyped(KeyEvent e) {

    }
    //endregion
    //endregion

    enum Mousebutton {
        Left,
        Right
    }
    public static final ArrayRealVector mousepos = new ArrayRealVector(new Double[]{0.0, 0.0});
    static InputInfo input = new InputInfo();

    static {
        input.mousepos = mousepos;
        for (int i = 0; i <= 0xE3; i++) {
            keyPressEvents.put(i, false);
            keyReleaseEvents.put(i, false);
        }
    }

    public static void handleInput() {
        input.reset();
        GameState state = GameHandler.getState();
        //region Always
        if (TurtleMenu.getFocused() != null) {
            if (pressed(VK_ESCAPE)) {
                TurtleMenu.unfocus();
            } else if (TurtleMenu.getFocused() instanceof Textbox textbox) {
                if (pressed(VK_ENTER)) {
                    textbox.useValue();
                    TurtleMenu.unfocus();
                    dispatchText();
                } else if (pressed(VK_BACK_SPACE)) {
                    if (textbox.text.length() > 0) {
                        textbox.text = textbox.text.substring(0, textbox.text.length() - 1);
                    }
                    unpress(VK_BACK_SPACE);
                } else if (textbox.text.length() < DevConfig.maxNameLength) {
                    textbox.text = textbox.text + getText();
                    if (textbox.text.length() > DevConfig.maxNameLength) {
                        textbox.text = textbox.text.substring(0, DevConfig.maxNameLength - 1);
                    }
                }
            }
        }
        if (released(VK_ESCAPE)) {
            GameHandler.escape();
            dispatch(VK_ESCAPE);
        }
        if (pressed(VK_SPACE)) {
            GameHandler.debug = true;
            unpress(KeyEvent.VK_SPACE);
        }
        if (released(VK_SPACE)) {
            GameHandler.debug = false;
            unrelease(KeyEvent.VK_SPACE);
        }
        //endregion
        //region Menu
        if (pressed(Left)) {
            if (TurtleMenu.press(mousepos)) {
                unpress(Left);
            }
        }
        if (released(Left)) {
            if (TurtleMenu.release()) {
                dispatch(Left);
            }
        }
        //endregion
        //region Play
        if (state == playClient || state == playServer) {
            if (pressed(Left)) {
                input.drag();
            }
            if (released(Left)) {
                dispatch(Left);
            }
            if (pressed(VK_C)) {
                input.create();
                dispatch(KeyEvent.VK_C);
            }
            if (released(Right)) {
                input.webFling();
                dispatch(Right);
            }
            if (released(VK_D)) {
                input.detachWeb();
                dispatch(VK_D);
            }
            if (pressed(VK_S)) {
                TurtleMenu.showScores();
                TurtleMenu.refreshScores();
            } else {
                TurtleMenu.hideScores();
            }
            if (released(VK_S)) {
                dispatch(VK_S);
            }
        } else {
            dispatch(Left);
        }
        //endregion
    }


    private static void dispatchText(){
        for (int key = 0x2C; key < 0x69 + 1; key++) {
            dispatch(key);
        }
        dispatch(VK_ENTER);
        dispatch(VK_SHIFT);
    }
    @NotNull
    private static StringBuilder getText() { //actually do this at some point
        StringBuilder text = new StringBuilder();
        for (int key = 0x2C; key < 0x69 + 1; key++) {
            if (pressed(key)) {
                text.append(KeyEvent.getKeyText(key));
                unpress(key);
            }
        }
        if (released(VK_SHIFT)) {
            dispatch(VK_SHIFT);
        }
        if (!pressed(VK_SHIFT)) {
            text = new StringBuilder(text.toString().toLowerCase());
        }
        return text;
    }

    private static Mousebutton getButton(int button) {
        if (button == MouseEvent.BUTTON1) {
            return Left;
        } else {
            return Right;
        }
    }

    public static InputInfo getInput() {
        return input;
    }

    //region Private key/mousebutton getters and setters
    private static boolean pressed(int key) {
        return keyPressEvents.get(key);
    }

    private static boolean released(int key) {
        return keyReleaseEvents.get(key);
    }

    private static void unrelease(int key) {
        keyReleaseEvents.put(key, false);
    }

    private static void unpress(int key) {
        keyPressEvents.put(key, false);
    }

    private static void dispatch(int key) {
        keyReleaseEvents.put(key, false);
        keyPressEvents.put(key, false);
    }

    private static boolean pressed(Mousebutton button) {
        return mousePressEvents.get(button) != null;
    }

    private static boolean released(Mousebutton button) {
        return mouseReleaseEvents.get(button) != null;
    }

    private static void unrelease(Mousebutton button) {
        mouseReleaseEvents.put(button, null);
    }

    private static void unpress(Mousebutton button) {
        mousePressEvents.put(button, null);
    }

    private static void dispatch(Mousebutton button) {
        mouseReleaseEvents.put(button, null);
        mousePressEvents.put(button, null);
    }
    //endregion
}
