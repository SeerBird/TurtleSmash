package game;

import game.connection.*;
import game.connection.packets.GameStartPacket;
import game.connection.packets.containers.ServerStatus;
import game.input.InputControl;
import game.input.InputInfo;
import game.connection.packets.ServerPacket;
import game.output.GameWindow;
import game.output.Renderer;
import game.output.audio.Sound;
import game.output.ui.rectangles.Textbox;
import game.output.ui.TurtleMenu;
import game.world.World;
import game.world.bodies.Body;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GameHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static GameState state;
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

    private static final ArrayList<Job> toRemove = new ArrayList<>();
    private static final ArrayList<Job> toAdd = new ArrayList<>();

    private static final ArrayList<Runnable> jobs = new ArrayList<>();
    private static final ArrayList<Player> players = new ArrayList<>();//player 0 is local
    private static final ArrayList<Player> removedPlayers = new ArrayList<>();
    public static final ServerPacket lastPacket = new ServerPacket();
    public static final Map<InetAddress, ServerStatus> servers = new HashMap<>();
    static ServerTCP tcpServer;
    static ClientTCP tcpClient;
    static final GameWindow window = new GameWindow();
    static final Sound sound = new Sound();
    // idk what this has become, seems dodgy. I should move this to the actual input module
    // figure out what the different state functions are and whether the order matters, then see

    public static boolean debug;

    static {
        //important stuff
        state = GameState.main;

        //jobs
        job.clear();
        job.put(Job.sendClient, () -> sendClientPacket());
        job.put(Job.updateWorld, () -> World.update());
        job.put(Job.menuInput, () -> InputControl.handleMenuInput());
        job.put(Job.gameInput, () -> InputControl.getGameInput());
        job.put(Job.handlePlayers, () -> handlePlayers());
        job.put(Job.sendServer, () -> broadcastServerPacket());
        job.put(Job.handleServerPacket, () -> handleServerPacket());
        job.put(Job.updateMenu, () -> TurtleMenu.update());

        //starting state
        addJob(Job.menuInput);
        addJob(Job.gameInput);
        addJob(Job.updateMenu);
        addJob(Job.handlePlayers);
        addJob(Job.updateWorld);
        players.clear();
        Player p = new Player(); // the player on this device
        p.connectInput(InputControl.getInput());
        players.add(p);
    }

    public static void out() {
        Renderer.drawImage(window.getCanvas());
        Renderer.drawImage(window.getCanvas());
        window.showCanvas();
    }

    public static void update() {
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

    public static void addJob(Job job) {
        toAdd.add(job);
    }

    public static void removeJob(Job job) {
        toRemove.add(job);
    }

    //Jobs


    private static void handlePlayers() {
        for (Player ghost : removedPlayers) {
            players.remove(ghost);
        }
        InputInfo input;
        Body body;
        for (Player player : players) {
            input = player.getInput();
            if ((body = player.getBody()) != null) {
                if (input.teleport) {
                    body.shift((ArrayRealVector) body.getDistance(input.mousepos).mapMultiply(0.2));
                    body.stop();
                }
                if (input.create) {
                    World.spawn(input.mousepos);
                }
                if (input.webFling) {
                    player.getBody().webFling(input.mousepos.copy());
                }
            }
        }
    }

    private static void broadcastServerPacket() {//ServerPacket should be assembled piece by piece, redo
        ServerPacket packet = new ServerPacket(players);
        synchronized (players) {
            for (Player player : players) {//potential for sending different info
                player.send(packet);
            }
        }
    }

    private static void sendClientPacket() {
        tcpClient.send(players.get(0).input);
    }

    private static void handleServerPacket() {
        if (lastPacket.changed) {
            synchronized (lastPacket) {
                World.set(lastPacket.world);
                lastPacket.changed = false;
            }
        }
    }

    public static void receiveServerPacket(ServerPacket packet) {
        lastPacket.set(packet);
    }

    //States
    public static void host() {
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
        tcpServer = new ServerTCP(port);
        tcpServer.start();
        Broadcaster.setStatus("bababoi", port);
        Broadcaster.start();
        addJob(Job.sendServer);
    }

    public static void playServer() {
        setState(GameState.playServer);
        World.startGen();
        GameStartPacket packet = new GameStartPacket();
        synchronized (players) { // make this a procedure
            for (int i = 1; i < players.size(); i++) {//potential for sending different info
                players.get(i).send(packet);//code proper disconnect handling, breaks at the moment
            }
        }
    }

    public static void discover() {
        setState(GameState.discover);
        Discovery.start(servers);
    }

    public static void connect(ServerStatus server) {
        setState(GameState.lobby);
        tcpClient = new ClientTCP(server);
        tcpClient.start();
        addJob(Job.handleServerPacket);
        addJob(Job.sendClient);
    }

    public static void disconnectTCPClient() {
        tcpClient.disconnect();
    }

    public static void playClient() {
        setState(GameState.playClient);
    }

    public static void playToDiscover() {
        setState(GameState.discover);
        removeJob(Job.sendClient);
    }

    public static void playToHost() {
        setState(GameState.host);
    }

    public static void hostToMain() {
        setState(GameState.main);
        Broadcaster.stop();
        tcpServer.disconnect();
    }

    public static void lobbyToDiscover() {
        setState(GameState.discover);
    }

    public static void discoverToMain() {
        setState(GameState.main);
        Discovery.stop();
    }

    private static void setState(GameState gameState) {
        state = gameState;
        TurtleMenu.refreshGameState();
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

    public static void refreshLAN() {
        for (InetAddress address : servers.keySet()) {
            if (System.nanoTime() - servers.get(address).nanoTime > Config.discoveryMilliTimeout * 1000000) {
                servers.remove(address);
            }
        }
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
            TurtleMenu.popup(cause.getMessage());
        }
    }

    //Simple stuff
    @NotNull
    public static Player addPlayer(SocketChannel channel) {
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
            dupe = new Player();
            players.add(dupe);
        }
        dupe.setChannel(channel);
        return dupe;
    }

    private static void removePlayer(Player player) {
        removedPlayers.add(player);
    }

    @Nullable
    public static Player getPlayer() {
        if (players.size() == 0) {
            return null;
        }
        return players.get(0);
    }

    // Input


    //getters

    public static GameState getState() {
        return state;
    }
}
