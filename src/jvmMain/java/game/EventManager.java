package game;

import game.connection.TurtleClient;
import game.connection.TurtleServer;
import game.input.MenuClickEvent;
import game.input.MouseInput;
import game.output.GameWindow;
import game.output.Renderer;
import game.output.audio.Sound;
import game.output.ui.TurtleMenu;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Web;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class EventManager {
    GameWindow win;
    Sound sound;
    World world;
    Renderer renderer;
    TurtleMenu menu;
    TurtleServer server;
    TurtleClient connection;
    ArrayList<Runnable> jobs;
    ArrayList<Runnable> toRemove;
    ArrayList<Runnable> toAdd;
    // idk what this has become, seems dodgy
    private final Map<Integer, Boolean> keyPressedEvents;
    private final Map<Integer, Boolean> keyReleasedEvents;
    private MenuClickEvent menuClickEvent;
    private final HashMap<Integer, MouseEvent> mousePressEvents;
    private final HashMap<Integer, MouseEvent> mouseReleaseEvents;
    private MouseEvent mouseMoveEvent;
    ArrayRealVector mousepos;
    public final Map<Integer, Boolean> USED_KEYS = Map.ofEntries(entry(KeyEvent.VK_W, false), entry(KeyEvent.VK_A, false), entry(KeyEvent.VK_S, false), entry(KeyEvent.VK_D, false), entry(KeyEvent.VK_SPACE, false), entry(KeyEvent.VK_SHIFT, false), entry(KeyEvent.VK_CONTROL, false), entry(KeyEvent.VK_P, false));

    public EventManager() {
        // same thing. wtf.
        keyPressedEvents = new HashMap<>();
        keyReleasedEvents = new HashMap<>();
        mousePressEvents = new HashMap<>();
        mouseReleaseEvents = new HashMap<>();
        keyPressedEvents.putAll(USED_KEYS);
        keyReleasedEvents.putAll(USED_KEYS);
        mousepos = new ArrayRealVector(new Double[]{400.0, 400.0});


        jobs = new ArrayList<>();
        toAdd = new ArrayList<>();
        toRemove = new ArrayList<>();
        menu = new TurtleMenu(this);
        sound = new Sound();
        world = new World(this);
        win = new GameWindow(this);
        renderer = new Renderer(this);
        server = new TurtleServer(this);
        connection = new TurtleClient(this);
    }

    public void terminate() {
        // you think you can stop me?
    }
    private ArrayList<Runnable> storedJobs;
    public void pause(){

    }

    public void out() {
        renderer.drawImage(win.getCanvas(), world);
        renderer.drawImage(win.getCanvas(), menu);
        win.showCanvas();
    }

    public void update() {
        {
            for (Runnable job : toRemove) {
                if (!jobs.contains(job)) {
                    jobs.add(job);
                }
            }
            toRemove.clear();
            for (Runnable job : toAdd) {
                jobs.remove(job);
            }
            toAdd.clear();
        }// remove and add jobs
        for (Runnable job : jobs) {
            job.run();
        }// get em done
    }

    public void addJob(Runnable job) {
        toAdd.add(job);
    }

    public void removeJob(Runnable job) {
        toRemove.remove(job);
    }

    private void handleMenuInput() {

    }

    private void handleGameInput() {
        if (mousePressEvents.get(MouseInput.LEFT) != null) {
            Body player = world.getPlayer();
            if (player != null) {
                ArrayRealVector dist = world.getDistance(player.getCenter(), mousepos);
                player.shift(dist);
                player.stop();
            }
        }
        if (mouseReleaseEvents.get(MouseInput.LEFT) != null) {
            mousePressEvents.remove(1);
            mouseReleaseEvents.remove(1);
        }
        if (keyPressedEvents.get(KeyEvent.VK_SPACE)) {
            keyPressedEvents.put(KeyEvent.VK_SPACE, false);
        }
        if (keyPressedEvents.get(KeyEvent.VK_P)) {
            world.testgen();
            keyPressedEvents.put(KeyEvent.VK_P, false);
        }
    }

    private void checkLAN() {

    }

    public void post(AWTEvent e) {

    }
    // Input

    public void postKeyPressedEvent(@NotNull KeyEvent e) {
        keyPressedEvents.put(e.getKeyCode(), true);
    }

    public ArrayRealVector getMousepos() {
        return mousepos;
    }

    public void postKeyReleasedEvent(@NotNull KeyEvent e) {
        keyReleasedEvents.put(e.getKeyCode(), true);
    }

    public void postWindowResizeEvent(ComponentEvent e) {
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

    public Renderer getRenderer() {
        return this.renderer;
    }
}
