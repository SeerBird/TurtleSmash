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

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GameHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private GameState state;
    private static final ArrayList<Player> players = new ArrayList<>();//player 0 is local
    private static final ArrayList<Player> removedPlayers = new ArrayList<>();
    private static final ServerPacket lastPacket = new ServerPacket();
    private static final Map<InetAddress, ServerStatus> servers = new HashMap<>();
    private static final HashMap<Job, Runnable> job = new HashMap<>();

    private enum Job {
        sendClient,
        sendServer,
        handlePlayers,
        menuInput,
        textInput,
        handleServerPacket,
        updateMenu,
        gameInput,
        updateWorld
    }

    private static final ArrayList<Runnable> jobs = new ArrayList<>();
    private static final ArrayList<Job> toRemove = new ArrayList<>();
    private static final ArrayList<Job> toAdd = new ArrayList<>();
    Renderer renderer;
    TurtleMenu menu;
    ServerTCP tcpServer;
    ClientTCP tcpClient;
    Multicaster multicaster;
    GameWindow window;
    Sound sound;
    World world;
    // idk what this has become, seems dodgy. I should move this to the actual input module
    // figure out what the different state functions are and whether the order matters, then see
    private static final Map<Integer, Boolean> keyPressedEvents = new HashMap<>();
    private static final Map<Integer, Boolean> keyReleasedEvents = new HashMap<>();
    private static final Map<Integer, MouseEvent> mousePressEvents = new HashMap<>();
    private static final Map<Integer, MouseEvent> mouseReleaseEvents = new HashMap<>();
    private MouseEvent mouseMoveEvent;
    private static final ArrayRealVector mousepos = new ArrayRealVector(2);

    public GameHandler() {
        // weird? inputs
        keyPressedEvents.clear();
        for (int i = 0x10; i <= 0xE3; i++) {
            keyPressedEvents.put(i, false);
            keyReleasedEvents.put(i, false);
        }

        //important stuff
        state = GameState.main;
        menu = new TurtleMenu(this, lastPacket, servers);
        sound = new Sound();
        world = new World(this);
        renderer = new Renderer(this);
        window = new GameWindow(this);

        //jobs
        job.clear();
        job.put(Job.sendClient, () -> this.sendClientPacket());
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
        players.clear();
        players.add(new Player(this));
    }

    public void out() {
        renderer.drawImage(window.getCanvas(), world);
        renderer.drawImage(window.getCanvas(), menu);
        window.showCanvas();
    }

    public void update() {
        {
            for (Job added : toAdd) {
                jobs.add(job.get(added));
            }
            toAdd.clear();
            for (Job removed : toRemove) {
                jobs.remove(job.get(removed));
            }
            toRemove.clear();
        }// remove and add jobs
        {
            for (Runnable job : jobs) {
                job.run();
            }
        }// get em done
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
            players.get(0).getInput().teleport = mousepos; //copy?
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
                    body.stop();
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
                lastPacket.changed = false;
            }
        }
    }

    public void receiveServerPacket(ServerPacket packet) {
        lastPacket.set(packet);
    }

    //States
    public void host() {
        int port = 0;
        {
            ServerSocket socket = null;
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
                        logger.severe("What the gleeck??? " + e.getMessage() +
                                "\nThis is unbelievable. Failed to close a socket. wow. just wow.");
                    }
                }
            }
            if (port == 0) {
                logger.severe("Failed to find a free port");
                return;
            }
        }// find a free port
        setState(GameState.host);
        tcpServer = new ServerTCP(this, port);
        tcpServer.start();
        multicaster = new Multicaster(Multiplayer.multicastIP, true, servers);
        multicaster.setServerStatus("bababoi", port);
        multicaster.start();
        addJob(Job.sendServer);
    }

    public void playServer() {
        setState(GameState.playServer);
        world.startGen();
        GameStartPacket packet = new GameStartPacket();
        synchronized (players) {
            for (int i = 1; i < players.size(); i++) {//potential for sending different info
                players.get(i).getChannel().writeAndFlush(packet);//code proper disconnect handling, breaks at the moment
            }
        }
    }

    public void discover() {
        setState(GameState.discover);
        multicaster = new Multicaster(Multiplayer.multicastIP, false, servers);
        multicaster.start();
    }

    public void connect(ServerStatus server) {
        setState(GameState.lobby);
        tcpClient = new ClientTCP(this, server);
        tcpClient.start();
        addJob(Job.handleServerPacket);
        addJob(Job.sendClient);
    }

    public void playClient() {
        setState(GameState.playClient);
    }

    public void playToDiscover() {
        setState(GameState.discover);
        removeJob(Job.sendClient);
    }

    public void playToHost() {
        setState(GameState.host);
    }

    public void hostToMain() {
        setState(GameState.main);
        multicaster.disconnect();
        tcpServer.disconnect();
    }

    private void lobbyToDiscover() {
        setState(GameState.discover);
    }

    private void discoverToMain() {
        setState(GameState.main);
        multicaster.disconnect();
    }

    private void setState(GameState state) {
        this.state = state;
        menu.refreshGameState();
    }

    Textbox textbox;

    public void enterTextbox(Textbox textbox) {//move to menu
        this.textbox = textbox;
        addJob(Job.textInput);
    }

    public void leaveTextbox() {
        removeJob(Job.textInput);
    }

    private void getTextInput() {//move to input
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

    public void postMousePressEvent(MouseEvent e) {
        mousePressEvents.put(e.getButton(), e);
    }

    public void postMouseReleaseEvent(MouseEvent e) {
        mouseReleaseEvents.put(e.getButton(), e);
    }

    public void postMouseMoveEvent(@NotNull MouseEvent e) {
        mouseMoveEvent = e;
        mousepos.setEntry(0, e.getPoint().x);
        mousepos.setEntry(1, e.getPoint().y);
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
}
