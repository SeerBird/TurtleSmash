package game;

import game.connection.*;
import game.connection.packets.GameStartPacket;
import game.connection.packets.containers.ServerStatus;
import game.input.InputControl;
import game.input.InputInfo;
import game.connection.packets.ServerPacket;
import game.output.GameWindow;
import game.output.Renderer;
import game.output.ui.TurtleMenu;
import game.world.World;
import game.world.bodies.Body;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

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
    //region Jobs
    private static final HashMap<Job, Runnable> job = new HashMap<>();

    private enum Job {
        sendClient,
        sendServer,
        handlePlayers,
        handleInput,
        handleServerPacket,
        updateMenu,
        updateWorld,
        revivePlayers
    }

    private static final ArrayList<Job> toRemove = new ArrayList<>();
    private static final ArrayList<Job> toAdd = new ArrayList<>();

    private static final ArrayList<Runnable> jobs = new ArrayList<>();
    //endregion
    //region Players
    private static final ArrayList<Player> players = new ArrayList<>();//player 0 is local
    private static final ArrayList<Player> removedPlayers = new ArrayList<>();
    private static final ArrayList<Player> deadPlayers = new ArrayList<>();
    private static final ArrayList<Player> revivedPlayers = new ArrayList<>();
    //endregion
    //region Connection
    public static final ServerPacket lastPacket = new ServerPacket();
    public static final Map<InetAddress, ServerStatus> servers = new HashMap<>();
    static ServerTCP tcpServer;
    static ClientTCP tcpClient;
    //endregion
    static final GameWindow window = new GameWindow();
    public static boolean debug;

    static {
        state = GameState.main;
        //region Define job dictionary
        job.clear();
        job.put(Job.sendClient, () -> sendClientPacket());
        job.put(Job.updateWorld, () -> World.update());
        job.put(Job.handleInput, () -> InputControl.handleInput());
        job.put(Job.handlePlayers, () -> handlePlayers());
        job.put(Job.sendServer, () -> broadcastServerPacket());
        job.put(Job.handleServerPacket, () -> handleServerPacket());
        job.put(Job.updateMenu, () -> TurtleMenu.update());
        job.put(Job.revivePlayers, () -> revivePlayers());
        //endregion
        //region Set starting state
        addJob(Job.handleInput);
        addJob(Job.updateMenu);
        addJob(Job.handlePlayers);
        addJob(Job.updateWorld);
        players.clear();
        Player p = new Player(); // the player on this device
        p.connectInput(InputControl.getInput());
        players.add(p);
        //endregion
        mainToHost();
        hostToPlayServer();
    }

    public static void out() {
        Renderer.drawImage(window.getCanvas());
        Renderer.drawImage(window.getCanvas());
        window.showCanvas();
    }

    public static void update() {
        //region remove and add jobs
        for (Job added : toAdd) {
            jobs.add(job.get(added));
        }
        toAdd.clear();
        for (Job removed : toRemove) {
            jobs.remove(job.get(removed));
        }
        toRemove.clear();
        //endregion
        //region get em done
        for (Runnable job : jobs) {
            job.run();
        }
        //endregion
    }

    //region Job Methods
    public static void addJob(Job job) {
        toAdd.add(job);
    }

    public static void removeJob(Job job) {
        toRemove.add(job);
    }

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
                    //World.playerSpawn(player);
                }
            }
        }
    }

    private static void revivePlayers() {
        for (Player player : deadPlayers) {
            player.deathTimer -= 1;
            if (player.deathTimer < 0) {
                World.playerSpawn(player);
                revivedPlayers.add(player);
            }
        }
        for(Player player:revivedPlayers){
            deadPlayers.remove(player);
        }
        revivedPlayers.clear();
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
    //endregion

    //region State traversal
    private static void setState(GameState gameState) {
        state = gameState;
        TurtleMenu.refreshGameState();
    }

    public static GameState getState() {
        return state;
    }

    public static void mainToHost() {
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

    public static void hostToPlayServer() {
        setState(GameState.playServer);
        World.startGen();
        GameStartPacket packet = new GameStartPacket();
        synchronized (players) { // make this a procedure
            for (int i = 1; i < players.size(); i++) {//potential for sending different info
                players.get(i).send(packet);//code proper disconnect handling, breaks at the moment
            }
        }
        addJob(Job.revivePlayers);
    }

    public static void playServerToHost() {
        setState(GameState.host);
        removeJob(Job.revivePlayers);
    }

    public static void hostToMain() {
        setState(GameState.main);
        Broadcaster.stop();
        tcpServer.disconnect();
    }

    public static void mainToDiscover() {
        setState(GameState.discover);
        Discovery.start(servers);
    }

    public static void discoverToLobby(ServerStatus server) {
        setState(GameState.lobby);
        tcpClient = new ClientTCP(server);
        tcpClient.start();
        addJob(Job.handleServerPacket);
        addJob(Job.sendClient);
    }

    public static void lobbyToDiscover() {
        setState(GameState.discover);
    }

    public static void lobbyToPlayClient() {
        setState(GameState.playClient);
    }

    public static void playClientToDiscover() {
        setState(GameState.discover);
        removeJob(Job.sendClient);
    }

    public static void discoverToMain() {
        setState(GameState.main);
        Discovery.stop();
    }
//endregion

    //region Connection
    public static void disconnectTCPClient() {
        tcpClient.disconnect();
    }

    public static void refreshLAN() {
        for (InetAddress address : servers.keySet()) {
            if (System.nanoTime() - servers.get(address).nanoTime > Config.discoveryMilliTimeout * 1000000) {
                servers.remove(address);
            }
        }
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
    //endregion

    //region Player handling
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

    public static void killPlayer(Player player) {
        deadPlayers.add(player);
        player.deathTimer = Config.deathFrames;
    }

    //endregion
    public void terminate() {
        // you think you can stop me?
    }
}
