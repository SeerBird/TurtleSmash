package game;

import game.connection.*;
import game.connection.packets.GameStartPacket;
import game.connection.packets.containers.ServerStatus;
import game.input.InputInfo;
import game.connection.packets.ServerPacket;
import game.input.MouseInput;
import game.output.GameWindow;
import game.output.Renderer;
import game.output.audio.Sound;
import game.output.ui.rectangles.Textbox;
import game.output.ui.TurtleMenu;
import game.util.Multiplayer;
import game.world.World;
import game.world.bodies.Body;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EventManager {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    GameState state;
    GameWindow win;
    Sound sound;
    World world;
    private static final ArrayList<Player> players = new ArrayList<>();//player 0 is local
    private static final ArrayList<Player> removedPlayers = new ArrayList<>();
    Renderer renderer;
    TurtleMenu menu;
    ServerTCP tcpServer;
    ClientTCP tcpClient;
    Multicaster multicaster;
    private static final ServerPacket lastPacket = new ServerPacket();
    private static final ArrayList<Runnable> jobs = new ArrayList<>();
    private static final ArrayList<Job> toRemove = new ArrayList<>();
    private static final ArrayList<Job> toAdd = new ArrayList<>();
    private static final Map<InetAddress, ServerStatus> servers = new HashMap<>();
    // idk what this has become, seems dodgy
    private final Map<Integer, Boolean> keyPressedEvents;
    private final Map<Integer, Boolean> keyReleasedEvents;
    private final HashMap<Integer, MouseEvent> mousePressEvents;
    private final HashMap<Integer, MouseEvent> mouseReleaseEvents;
    private MouseEvent mouseMoveEvent;
    ArrayRealVector mousepos;

    private enum Job {
        sendClient,
        sendServer,
        handlePlayers,
        menuInput,
        broadcastLAN,
        textInput,
        handleServerPacket,
        updateMenu,
        gameInput,
        updateWorld
    }

    private static final HashMap<Job, Runnable> job = new HashMap<>();

    public EventManager() {
        // weird? inputs
        keyPressedEvents = new HashMap<>();
        keyReleasedEvents = new HashMap<>();
        mousePressEvents = new HashMap<>();
        mouseReleaseEvents = new HashMap<>();
        for (int i = 0x10; i <= 0xE3; i++) {
            keyPressedEvents.put(i, false);
            keyReleasedEvents.put(i, false);
        }
        mousepos = new ArrayRealVector(new Double[]{400.0, 400.0});

        //important stuff
        state = GameState.main;
        menu = new TurtleMenu(this, lastPacket, servers);
        sound = new Sound();
        world = new World(this);
        renderer = new Renderer(this);
        win = new GameWindow(this);
        players.clear();

        //jobs
        job.clear();
        job.put(Job.sendClient, () -> this.sendClientPacket());
        job.put(Job.broadcastLAN, () -> multicaster.broadcastToLan());
        job.put(Job.updateWorld, () -> world.update());
        job.put(Job.menuInput, () -> this.handleMenuInput());
        job.put(Job.gameInput, () -> this.getGameInput());
        job.put(Job.handlePlayers, () -> this.handlePlayers());
        job.put(Job.sendServer, () -> this.broadcastServerPacket());
        job.put(Job.handleServerPacket, () -> this.handleServerPacket());
        job.put(Job.updateMenu, () -> menu.update());

        //starting state
        addJob(Job.menuInput);
        addJob(Job.gameInput);
        addJob(Job.updateMenu);
        addJob(Job.handlePlayers);
        addJob(Job.updateWorld);
        players.add(new Player(this));
    }

    public void out() {
        renderer.drawImage(win.getCanvas(), world);
        renderer.drawImage(win.getCanvas(), menu);
        win.showCanvas();
    }

    public void update() {
        {
            for (Job job : toAdd) {
                jobs.add(this.job.get(job));
            }
            toAdd.clear();
            for (Job job : toRemove) {
                jobs.remove(this.job.get(job));
            }
            toRemove.clear();
        }// remove and add jobs
        for (Runnable job : jobs) {
            job.run();
        }// get em done
        lastPacket.changed = false;// put this somewhere else, looks ugly
    }

    public void addJob(Job job) {
        toAdd.add(job);
    }

    public void removeJob(Job job) {
        toRemove.add(job);
    }

    //Jobs

    private void handleMenuInput() {
        if (mousePressEvents.get(MouseInput.LEFT) != null) {
            if (menu.press(mousepos)) {
                mousePressEvents.put(MouseInput.LEFT, null);
            }
        }
        if (mouseReleaseEvents.get(MouseInput.LEFT) != null) {
            if (menu.release()) {
                mouseReleaseEvents.put(MouseInput.LEFT, null);
            }
        }
        if (keyPressedEvents.get(KeyEvent.VK_ESCAPE)) {
            if (state == GameState.playClient) {
                tcpClient.disconnect();
            } else if (state == GameState.lobby) {
                lobbyToDiscover();
            } else if (state == GameState.discover) {
                discoverToMain();
            } else if (state == GameState.playServer) {
                playToHost();
            } else if (state == GameState.host) {
                hostToMain();
            }
            keyPressedEvents.put(KeyEvent.VK_ESCAPE, false);
        }
    }

    private void getGameInput() {
        players.get(0).getInput().reset();
        if (mousePressEvents.get(MouseInput.LEFT) != null) {
            players.get(0).getInput().teleport = mousepos;//copy?
        }
        if (mouseReleaseEvents.get(MouseInput.LEFT) != null) {
            mousePressEvents.put(MouseInput.LEFT, null);
            mouseReleaseEvents.put(MouseInput.LEFT, null);
        }
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
                if (input.teleport != null) {
                    body.shift((ArrayRealVector) body.getDistance(input.teleport).mapMultiply(0.2));
                }
            }
        }
    }

    private void broadcastServerPacket() {//ServerPacket should be assembled piece by piece, redo
        ServerPacket packet = new ServerPacket(world, players);
        synchronized (players) {
            for (Player player : players) {//potential for sending different info
                player.send(packet);
            }
        }
    }

    private void sendClientPacket() {
        tcpClient.send(players.get(0).input);
    }

    private void handleServerPacket() {
        if (lastPacket.changed) {
            synchronized (lastPacket) {
                this.world.set(lastPacket.world);
            }
        }
    }

    public void receiveServerPacket(ServerPacket packet) {
        lastPacket.set(packet);
    }

    //States
    public void host() {
        ServerSocket socket = null;
        int port = 0;
        try {
            socket = new ServerSocket(0);
            port = socket.getLocalPort();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.severe("What the gluck??? " + e.getMessage() +
                            "\nThis is unbelievable. Failed to close a socket. wow. just wow.");
                }
            }
        }
        if (port == 0) {
            logger.severe("Failed to find a free port");
            return;
        }
        state = GameState.host;
        menu.refreshGameState();
        tcpServer = new ServerTCP(this, port);
        tcpServer.start();
        multicaster = new Multicaster(Multiplayer.multicastIP, true, servers);
        multicaster.setServerStatus("bababoi", port);
        multicaster.start();
        addJob(Job.sendServer);
    }

    public void playServer() {
        state = GameState.playServer;
        menu.refreshGameState();
        world.startGen();
        GameStartPacket packet = new GameStartPacket();
        synchronized (players) {
            for (int i = 1; i < players.size(); i++) {//potential for sending different info
                players.get(i).getChannel().writeAndFlush(packet);
            }
        }
    }

    public void discover() {
        state = GameState.discover;
        menu.refreshGameState();
        multicaster = new Multicaster(Multiplayer.multicastIP, false, servers);
        multicaster.start();
    }

    public void connect(ServerStatus server) {
        state = GameState.lobby;
        menu.refreshGameState();
        tcpClient = new ClientTCP(this, server);
        tcpClient.start();
        addJob(Job.handleServerPacket);
        addJob(Job.sendClient);
    }

    public void playClient() {
        state = GameState.playClient;
        menu.refreshGameState();
    }

    public void playToDiscover() {
        state = GameState.discover;
        removeJob(Job.sendClient);
        menu.refreshGameState();
    }

    public void playToHost() {
        state = GameState.host;
        menu.refreshGameState();
    }

    public void hostToMain() {
        state = GameState.main;
        menu.refreshGameState();
        multicaster.disconnect();
        tcpServer.disconnect();
    }

    private void lobbyToDiscover() {
        state = GameState.discover;
        menu.refreshGameState();
    }

    private void discoverToMain() {
        state = GameState.main;
        multicaster.disconnect();
        menu.refreshGameState();
    }

    Textbox textbox;

    public void enterTextbox(Textbox textbox) {
        this.textbox = textbox;
        addJob(Job.textInput);
    }

    public void leaveTextbox() {
        removeJob(Job.textInput);
    }

    private void getTextInput() {
        //iterate through all text keys, get and reset pressed, check for shift and give the right letter to the textbox
    }

    public void refreshLAN() {
        multicaster.getServers();
    }

    public void terminate() {
        // you think you can stop me?
    }

    public void shutDownTCPServer() {
        removeJob(Job.sendServer);
    }

    public void shutDownTCPClient(Throwable cause) {
        removeJob(Job.sendClient);
        //menu.toMainManu();
        if (cause != null) {
            menu.popup(cause.getMessage());
        }
    }

    public void togglePause() {
        if (jobs.contains((Runnable) world::update)) {
            jobs.remove((Runnable) world::update);
        } else {
            jobs.add(world::update);
        }
    }

    //Simple stuff
    public Player addPlayer(SocketChannel channel) {
        Player dupe = null;
        for (Player player : players) {
            if (player.getChannel() != null) {
                if (player.getChannel().remoteAddress().getAddress() == channel.remoteAddress().getAddress()) {
                    if (dupe == null) {
                        dupe = player;
                    } else {
                        removePlayer(player);
                    }
                }
            }
        }
        if (dupe == null) {
            dupe = new Player(this);
            players.add(dupe);
        }
        dupe.setChannel(channel);
        return dupe;
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

    //getters
    public Renderer getRenderer() {
        return this.renderer;
    }

    public World getWorld() {
        return this.world;
    }

    public GameState getState() {
        return state;
    }

    // World

}
