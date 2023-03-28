package seerbird.game;


import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import seerbird.game.input.MenuClickEvent;
import seerbird.game.output.GameWindow;
import seerbird.game.output.Renderer;
import seerbird.game.output.audio.Sound;
import seerbird.game.output.connection.Connector;
import seerbird.game.output.ui.Button;
import seerbird.game.output.ui.Menu;
import seerbird.game.world.Turtle;
import seerbird.game.world.Web;
import seerbird.game.world.World;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;

import static java.util.Map.entry;

/**
 *
 **/
public class EventManager {
    GameWindow win;
    Sound sound;
    GameState gameState;
    World world;
    Renderer cam;
    Menu menu;
    Connector connector;
    // idk what this has become, seems dodgy
    private final Map<Integer, Boolean> keyPressedEvents;
    private final Map<Integer, Boolean> keyReleasedEvents;
    private ComponentEvent resizeEvent;
    private MenuClickEvent menuClickEvent;
    private final HashMap<Integer, MouseEvent> mousePressEvents;
    private final HashMap<Integer, MouseEvent> mouseReleaseEvents;
    private MouseEvent mouseMoveEvent;
    public boolean paused;
    public final Map<Integer, Boolean> USED_KEYS = Map.ofEntries(entry(KeyEvent.VK_W, false), entry(KeyEvent.VK_A, false), entry(KeyEvent.VK_S, false), entry(KeyEvent.VK_D, false), entry(KeyEvent.VK_SPACE, false), entry(KeyEvent.VK_SHIFT, false), entry(KeyEvent.VK_CONTROL, false));

    public EventManager() {
        gameState = GameState.Game; // SHOULD BE MENU
        menu = new Menu(this);
        sound = new Sound();
        world = new World(this);
        win = new GameWindow(this);
        cam = new Renderer(this);
        connector = new Connector();

        // same thing. wtf.
        keyPressedEvents = new HashMap<>();
        keyReleasedEvents = new HashMap<>();
        mousePressEvents = new HashMap<>();
        mouseReleaseEvents = new HashMap<>();
        keyPressedEvents.putAll(USED_KEYS);
        keyReleasedEvents.putAll(USED_KEYS);
        paused = false;
    }

    public void terminate() {
    }

    public void out() {
        if (gameState == GameState.Game) {
            cam.drawImage(win.getCanvas(), world);
            win.showCanvas();
        } else if (gameState == GameState.Menu) {
            cam.drawImage(win.getCanvas(), menu);
            win.showCanvas();
        }
    }

    public void update() {
        handleClientInput();
        if (gameState == GameState.Game) {
            if (!paused) {
                world.update();
            }
        } else if (gameState == GameState.Menu) {
            if (menuClickEvent != null) {
                if (menuClickEvent.getSource() instanceof Button) {
                    menuClickEvent = null;
                }
            }
            menu.update();
        }
    }

    private void handleClientInput() {
        if (resizeEvent != null) {
            cam.resize(resizeEvent.getComponent().getWidth(), resizeEvent.getComponent().getHeight());
            resizeEvent = null;
        }
        if (gameState == GameState.Game) {
            if (keyPressedEvents.get(KeyEvent.VK_SPACE)) {
                ArrayRealVector dist = world.getPlayer().getDistance(getMousePos());
                world.getPlayer().makeString((ArrayRealVector) dist.mapMultiply(Config.stringFling / dist.getNorm()));
                keyPressedEvents.put(KeyEvent.VK_SPACE, false);
            }
            /*
            if (keyReleasedEvents.get(KeyEvent.VK_SPACE)) {
                paused ^= true;
                keyPressedEvents.put(KeyEvent.VK_SPACE, false);
                keyReleasedEvents.put(KeyEvent.VK_SPACE, false);
            }
            */
        } else if (gameState == GameState.Menu) {

        }
    }

    public void post(AWTEvent e) {

    }
    // Input

    public void postKeyPressedEvent(@NotNull KeyEvent e) {
        keyPressedEvents.put(e.getKeyCode(), true);
    }

    public void postKeyReleasedEvent(@NotNull KeyEvent e) {
        keyReleasedEvents.put(e.getKeyCode(), true);
    }

    public void postWindowResizeEvent(ComponentEvent e) {
        resizeEvent = e;
    }

    public void postMenuClickEvent(MenuClickEvent e) {
        menuClickEvent = e;
    }

    public void postMousePressEvent(MouseEvent e) {
        mousePressEvents.put(e.getButton(), e);
    }

    public void postMouseReleaseEvent(MouseEvent e) {
        mouseReleaseEvents.put(e.getButton(), e);
    }

    public void postMouseMoveEvent(MouseEvent e) {
        mouseMoveEvent = e;
    }

    // World
    public void postStringFallOff(Web web) {

    }

    public void postTurtleBreak(Turtle t) {

    }

    private @NotNull ArrayRealVector getMousePos() {
        Point mouse = mouseMoveEvent.getPoint();
        return new ArrayRealVector(new Double[]{(double) mouse.x, (double) mouse.y});
    }

    public World getWorld() {
        return this.world;
    }
}
