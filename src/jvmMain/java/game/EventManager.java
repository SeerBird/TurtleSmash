package game;

import com.esotericsoftware.kryonet.Connection;
import game.connection.InputInfo;
import game.connection.ServerPacket;
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

import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventManager {
    GameWindow win;
    Sound sound;
    final World world;
    ArrayList<Player> players;
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
    //iterate, idiot
    //test
    ArrayList<InetAddress> potential;
    private ArrayList<Player> removedPlayers;

    public EventManager() {
        // weird inputs
        keyPressedEvents = new HashMap<>();
        keyReleasedEvents = new HashMap<>();
        mousePressEvents = new HashMap<>();
        mouseReleaseEvents = new HashMap<>();
        for (int i = 0x30; i <= 0xE3; i++) {
            keyPressedEvents.put(i, false);
            keyReleasedEvents.put(i, false);
        }
        mousepos = new ArrayRealVector(new Double[]{400.0, 400.0});

        //important stuff
        jobs = new ArrayList<>();
        toAdd = new ArrayList<>();
        toRemove = new ArrayList<>();
        menu = new TurtleMenu(this);
        sound = new Sound();
        world = new World(this);
        renderer = new Renderer(this);
        win = new GameWindow(this);
        server = new TurtleServer(this);
        connection = new TurtleClient(this);
        players = new ArrayList<>(); // player 0 is local
        removedPlayers = new ArrayList<>();

        //test
        potential = new ArrayList<>();

        //starting state
        addJob(this::handleMenuInput);
        addJob(menu::update);
        addJob(world::update);
        addJob(this::getGameInput);
        addJob(this::handlePlayers);
        players.add(new Player(this));
    }

    public void out() {
        renderer.drawImage(win.getCanvas(), world);
        renderer.drawImage(win.getCanvas(), menu);
        win.showCanvas();
    }

    public void update() {
        {
            for (Runnable job : toAdd) {
                if (!jobs.contains(job)) {
                    jobs.add(job);
                }
            }
            toAdd.clear();
            for (Runnable job : toRemove) {
                jobs.remove(job);
            }
            toRemove.clear();
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

    //Jobs

    private void handleMenuInput() {
        if (keyPressedEvents.get(KeyEvent.VK_U)) {
            keyPressedEvents.put(KeyEvent.VK_U, false);
            connection.startDiscoveringHosts();
            addJob(this::checkLAN);
        }
        if (keyPressedEvents.get(KeyEvent.VK_1)) {
            keyPressedEvents.put(KeyEvent.VK_1, false);
            if (potential.size() != 0) {
                connection.connect(potential.get(0));//blocks, make this into a thread
                addJob(this::sendInput);
            }
        }
        if (keyPressedEvents.get(KeyEvent.VK_H)) {
            keyPressedEvents.put(KeyEvent.VK_H, false);
            server.start();
            addJob(this::broadcastWorld);
        }
    }

    private void getGameInput() {
        if (mousePressEvents.get(MouseInput.LEFT) != null) {
            players.get(0).flingWeb(mousepos);
        }
        if (mouseReleaseEvents.get(MouseInput.LEFT) != null) {
            mousePressEvents.put(MouseInput.LEFT, null);
            mouseReleaseEvents.put(MouseInput.LEFT, null);
        }
        /*
        if (mouseReleaseEvents.get(MouseInput.LEFT) != null) {
            mousePressEvents.remove(MouseInput.LEFT);
            mouseReleaseEvents.remove(MouseInput.LEFT);
        }
        if (keyPressedEvents.get(KeyEvent.VK_SPACE)) {
            keyPressedEvents.put(KeyEvent.VK_SPACE, false);
        }
        if (keyPressedEvents.get(KeyEvent.VK_P)) {
            world.testgen();
            keyPressedEvents.put(KeyEvent.VK_P, false);
        }
         */
    }

    private void sendInput() {
        connection.send(players.get(0).input);
    }

    private void handlePlayers() {
        for (Player ghost : removedPlayers) {
            players.remove(ghost);
        }
        InputInfo input;
        for (Player player : players) {
            input = player.getInput();
            Body body;
            if ((body = player.getBody()) != null) {
                if (input.webFling != null) {
                    body.shift(body.getDistance(mousepos));
                }
            }
            input.reset();
        }
    }

    private void broadcastWorld() {//ServerPacket should be assembled piece by piece
        server.sendToAll(new ServerPacket(world));
    }

    private void checkLAN() {
        ArrayList<InetAddress> servers = connection.getHosts();
        if (servers != null) {
            if (servers.size() == 0) {
                // make a sad face
            } else {
                potential = servers;
            }
            removeJob(this::checkLAN);
            connection.resetHosts();
        }
    }

    public void terminate() {
        // you think you can stop me?
    }

    public void togglePause() {
        if (jobs.contains((Runnable) world::update)) {
            jobs.remove((Runnable) world::update);
        } else {
            jobs.add(world::update);
        }
    }

    //Simple stuff
    public void addPlayer(Connection connection) {
        Player dupe = null;
        for (Player player : players) {
            if (player.getConnection() != null) {
                if (player.getConnection().getRemoteAddressTCP() == connection.getRemoteAddressTCP()) {
                    if (dupe == null) {
                        dupe = player;
                    } else {
                        removePlayer(player);
                    }
                }
            }
        }
        if (dupe == null) {// low readability?
            dupe = new Player(this);
            players.add(dupe);
        }
        dupe.setConnection(connection);
    }

    private void removePlayer(Player player) {
        removedPlayers.add(player);
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

    public void setWorld(World world) {
        synchronized (this.world) {
            this.world.set(world);
        }
    }

    public Player getPlayer(Connection connection) {
        for (Player player : players) {
            if (player.getConnection() == connection) {
                return player;
            }
        }
        return null;
    }
}
