package seerbird.game;


import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.input.MenuClickEvent;
import seerbird.game.output.GameWindow;
import seerbird.game.output.Renderer;
import seerbird.game.output.audio.Sound;
import seerbird.game.output.connection.Connector;
import seerbird.game.output.ui.Button;
import seerbird.game.output.ui.Menu;
import seerbird.game.world.Web;
import seerbird.game.world.World;
import seerbird.game.world.bodies.Body;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;

import static java.util.Map.entry;

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
    ArrayRealVector mousepos;
    public final Map<Integer, Boolean> USED_KEYS = Map.ofEntries(entry(KeyEvent.VK_W, false), entry(KeyEvent.VK_A, false), entry(KeyEvent.VK_S, false), entry(KeyEvent.VK_D, false), entry(KeyEvent.VK_SPACE, false), entry(KeyEvent.VK_SHIFT, false), entry(KeyEvent.VK_CONTROL, false), entry(KeyEvent.VK_P, false));

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
        mousepos = new ArrayRealVector(new Double[]{(double) MouseInfo.getPointerInfo().getLocation().x, (double) MouseInfo.getPointerInfo().getLocation().y});
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
                for (int i = 0; i < CONSTANTS.worldStepsPerFrame; i++) {
                    world.update();
                }
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
            if (mousePressEvents.get(1) != null) {
                Body player = world.getPlayer();
                if (player != null) {
                    ArrayRealVector dist = player.getPoints().get(0).getDistance(mousepos);
                    player.getPoints().get(0).move(dist);
                    //player.stop();
                }
            }
            if (mouseReleaseEvents.get(1) != null) {
                mousePressEvents.remove(1);
                mouseReleaseEvents.remove(1);
            }
            if (keyPressedEvents.get(KeyEvent.VK_SPACE)) {
                paused ^= true;
                keyPressedEvents.put(KeyEvent.VK_SPACE, false);
            }
            if (keyPressedEvents.get(KeyEvent.VK_P)) {
                world.testgen();
                keyPressedEvents.put(KeyEvent.VK_P, false);
            }
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

    public void postMouseMoveEvent(@NotNull MouseEvent e) {
        mouseMoveEvent = e;
        if (mousepos != null) {
            mousepos.setEntry(0, e.getPoint().x);
            mousepos.setEntry(1, e.getPoint().y);
        }
    }

    // World
    public void postStringFallOff(Web web) {

    }

    public World getWorld() {
        return this.world;
    }
}
