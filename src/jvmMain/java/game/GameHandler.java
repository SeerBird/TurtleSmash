package game;

import game.connection.Broadcaster;
import game.connection.ClientTCP;
import game.connection.Discovery;
import game.connection.ServerTCP;
import game.connection.packets.ServerPacket;
import game.connection.packets.containers.LobbyData;
import game.connection.packets.containers.ServerStatus;
import game.connection.packets.containers.WorldData;
import game.connection.packets.containers.images.animations.AnimationImage;
import game.input.InputControl;
import game.input.InputInfo;
import game.output.GameWindow;
import game.output.Renderer;
import game.output.audio.Audio;
import game.output.audio.Sound;
import game.output.ui.TurtleMenu;
import game.util.DevConfig;
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
        revivePlayers,
        clearWorld
    }

    private static final ArrayList<Job> toRemove = new ArrayList<>();
    private static final ArrayList<Job> toAdd = new ArrayList<>();

    private static final ArrayList<Runnable> jobs = new ArrayList<>();
    //endregion
    //region Players
    private static final Map<Player, ServerPacket> players = new HashMap<>();//player 0 is local
    private static final Player host = new Player(Config.getPlayerName());
    ;//player 0 is local
    private static final ArrayList<Player> addedPlayers = new ArrayList<>();
    private static final ArrayList<Player> removedPlayers = new ArrayList<>();
    //endregion
    //region Connection
    public static final ServerPacket lastPacket = new ServerPacket(); // last received
    public static final Map<InetAddress, ServerStatus> servers = new HashMap<>();
    static ServerTCP tcpServer;
    static ClientTCP tcpClient;
    //endregion
    static final GameWindow window = new GameWindow();
    public static boolean debug;
    private static GameState state;

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
        job.put(Job.clearWorld, () -> {
            World.clear();
            World.update();
            removeJob(Job.clearWorld);
        });
        //endregion
        //region Set starting state
        addJob(Job.handleInput);
        addJob(Job.updateMenu);
        addJob(Job.handlePlayers);// the player on this device
        host.connectInput(InputControl.getInput());
        addPlayer(host);
        //endregion
    }

    public static void out() {
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

    //region Job Methods - merge some of them!
    public static void addJob(Job job) {
        toAdd.add(job);
    }

    public static void removeJob(Job job) {
        toRemove.add(job);
    }

    private static void handlePlayers() {
        for (Player added : addedPlayers) {
            players.put(added, new ServerPacket());
        }
        addedPlayers.clear();
        for (Player removed : removedPlayers) {
            players.remove(removed);
        }
        InputInfo input;
        Body body;
        for (Player player : players.keySet()) {
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
                if (input.detachWeb) {
                    player.getBody().detachWeb(input.mousepos.copy());
                }
            }
        }
    }

    private static void revivePlayers() {
        for (Player player : players.keySet()) {
            if (player.deathTimer > 0) {
                player.deathTimer -= 1;
                if (player.deathTimer <= 0) {
                    World.playerSpawn(player);
                }
            }
        }
    }


    /**
     * {@link #sendAnimation(Player, AnimationImage)}, {@link #sendSound(Player, Sound)} add data to each player's corresponding {@link ServerPacket}
     **/
    private static void broadcastServerPacket() {
        WorldData world = new WorldData(World.getBodies());
        boolean playing = state == GameState.playServer;
        //region reset
        for (Player player : players.keySet()) {
            if (players.get(player) == null) {
                players.put(player, new ServerPacket());
            }
        }
        //endregion
        for (Player recipient : players.keySet()) {
            players.get(recipient).lobby = new LobbyData(new ArrayList<>(players.keySet()), recipient); // repeated actions inside.
            players.get(recipient).world = world;
            players.get(recipient).playing = playing;
            recipient.send(players.get(recipient));
            players.get(recipient).clear();
        }
    }

    private static void sendClientPacket() {
        tcpClient.send(getHost().input);
    }

    private static void handleServerPacket() {
        if (lastPacket.changed) {
            synchronized (lastPacket) {
                if (state == GameState.lobby) {
                    if (TurtleMenu.lobbyWaiting()) {
                        if (lastPacket.playing) {
                            lobbyToPlayClient();
                            TurtleMenu.toggleLobbyWaiting();
                        }
                    }
                } else if (state == GameState.playClient) {
                    if (!lastPacket.playing) {
                        playClientToLobby();
                    }
                }
                setPlayers(lastPacket.lobby);
                World.set(lastPacket.world);
                for (AnimationImage<?> animation : lastPacket.animationImages) {
                    Renderer.addAnimation(animation.restoreAnimation());
                }
                for (Sound sound : lastPacket.sounds) {
                    Audio.playSound(sound);
                }
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
        Broadcaster.setPort(port);
        Broadcaster.setMessage(Config.getServerName());
        Broadcaster.start();
        addJob(Job.sendServer);
    }

    public static void hostToPlayServer() {
        setState(GameState.playServer);
        addJob(Job.revivePlayers);
        addJob(Job.updateWorld);
    }

    public static void playServerToHost() {
        setState(GameState.host);
        addJob(Job.clearWorld);
        for (Player player : players.keySet()) {
            player.die();
        }
        removeJob(Job.revivePlayers);
        removeJob(Job.updateWorld);
    }

    public static void hostToMain() {
        setState(GameState.main);
        Broadcaster.stop();
        removeJob(Job.sendServer);
        tcpServer.disconnect();
    }

    public static void mainToDiscover() {
        setState(GameState.discover);
        try {
            Discovery.start(servers);
        } catch (IOException e) {
            setState(GameState.main);
        }
    }

    public static void discoverToLobby(@NotNull ServerStatus server) {
        setState(GameState.lobby);
        tcpClient = new ClientTCP(server);
        tcpClient.start();
        addJob(Job.handleServerPacket);
        addJob(Job.sendClient);
    }

    public static void lobbyToDiscover() {
        setState(GameState.discover);
        removeJob(Job.sendClient);
        addJob(Job.clearWorld);
        tcpClient.disconnect();
    }

    public static void lobbyToPlayClient() {
        setState(GameState.playClient);
    }

    public static void playClientToDiscover() {
        setState(GameState.discover);
        removeJob(Job.sendClient);
        addJob(Job.clearWorld);
        tcpClient.disconnect();
    }

    public static void playClientToLobby() {
        setState(GameState.lobby);
    }

    public static void discoverToMain() {
        setState(GameState.main);
        Discovery.stop();
    }

    public static void escape() {
        if (state == GameState.playClient) {
            playClientToLobby();
        } else if (state == GameState.lobby) {
            lobbyToDiscover();
        } else if (state == GameState.discover) {
            discoverToMain();
        } else if (state == GameState.playServer) {
            playServerToHost();
        } else if (state == GameState.host) {
            hostToMain();
        }
    }
    //endregion

    //region Connection
    public static void refreshLAN() {
        for (InetAddress address : servers.keySet()) {
            if (System.nanoTime() - servers.get(address).nanoTime > DevConfig.discoveryMilliTimeout * 1000000) {
                servers.remove(address);
            }
        }
        TurtleMenu.refreshServerList(); // cringe. notify or whatever?
    }

    private static void setPlayers(@NotNull LobbyData lobby) {
        Player local = getHost();
        players.clear();
        players.put(local, new ServerPacket());
        for (String name : lobby.players) {
            if (name != null) {
                Player dummy = new Player(name);
                players.put(dummy, new ServerPacket());
            }
        }
    }

    public static Player getLocalPlayerFromServerId(Integer id) {
        ArrayList<String> playerNames = lastPacket.lobby.players;
        int localUser = playerNames.indexOf(null);
        if (id < localUser) {
            id += 1;
        } else if (id == localUser) {
            id = 0;
        }
        return GameHandler.getPlayers().get(id);
    }

    public static void broadcastSound(Sound sound) {
        for (ServerPacket packet : players.values()) {
            packet.sounds.add(sound);
        }
    }

    public static void broadcastAnimation(AnimationImage<?> animationImage) {
        for (ServerPacket packet : players.values()) {
            packet.animationImages.add(animationImage);
        }
    }

    public static void sendSound(Player recipient, Sound sound) {
        if (players.get(recipient) != null) {
            players.get(recipient).sounds.add(sound);
        }
    }

    public static void sendAnimation(Player recipient, AnimationImage<?> animation) {
        if (players.get(recipient) != null) {
            players.get(recipient).animationImages.add(animation);
        }
    }
    //endregion

    //region Player handling
    @NotNull
    public static Player connectPlayer(SocketChannel channel) {
        //region try to find the first player with the same address, remove any following players with the same address
        Player dupe = null;
        for (Player player : players.keySet()) {
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
        //endregion
        //region if no such player has been found, create one
        if (dupe == null) {
            dupe = new Player("bababoi" + players.size());
        }
        //endregion
        dupe.setChannel(channel); // set or change the channel to the newly connected one
        addPlayer(dupe);
        return dupe;
    }

    public static void addPlayer(Player player) {
        synchronized (addedPlayers) {
            addedPlayers.add(player);
        }
    }

    public static void removePlayer(Player player) {
        removedPlayers.add(player);
    }

    public static void killPlayer(Player player) {
        TurtleMenu.refreshScores();
    }

    public static boolean isHost(Player player) {
        return player == host;
    }

    public static Player getHost() {
        return host;
    }
    //endregion

    //region getters

    public static ArrayList<Player> getPlayers() {
        return new ArrayList<>(players.keySet());
    }

    public static Map<InetAddress, ServerStatus> getServers() {
        return servers;
    }

    public static ServerPacket getPacket() {
        return lastPacket;
    }

    //endregion
    public void terminate() {
        // you think you can stop me?
    }
}
