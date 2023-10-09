package game.output.ui;

import game.GameHandler;
import game.GameState;
import game.connection.packets.ServerPacket;
import game.connection.packets.containers.ServerStatus;
import game.output.ui.rectangles.GButton;
import game.output.ui.rectangles.PlayerList;
import game.output.ui.rectangles.ServerList;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TurtleMenu {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final ArrayList<IElement> elements = new ArrayList<>();
    private static final HashMap<GameState, ArrayList<IElement>> menuPresets = new HashMap<>();
    private static IElement pressed;
    //cringe element references
    static final ServerList serverList = new ServerList(200, 200, 600, 600, GameHandler.servers);
    static final PlayerList playerList = new PlayerList(100, 100, 600, 600, GameHandler.lastPacket);

    static{
        // Create the presets
        // main
        elements.add(new GButton(200, 200, 100, 100, GameHandler::discover, "Discover"));// discover UDP
        elements.add(new GButton(400, 200, 100, 100, GameHandler::host, "Host"));// host UDP and open TCP server
        savePreset(GameState.main);
        //host
        elements.add(new GButton(400, 200, 100, 100, GameHandler::playServer, "Play"));
        savePreset(GameState.host);
        //connect
        elements.add(serverList);
        savePreset(GameState.discover);
        //lobby
        elements.add(playerList);
        savePreset(GameState.lobby);
        //playServer
        savePreset(GameState.playServer);
        //playClient
        savePreset(GameState.playClient);
        refreshGameState();
    }

    public static boolean press(ArrayRealVector pos) {
        for (IElement element : elements) {
            if (element.press(pos)) {
                pressed = element;
                return true;
            }
        }
        return false;
    }

    public static GameState getState() {
        return GameHandler.getState();
    }

    public static void refreshGameState() {
        elements.clear();
        elements.addAll(menuPresets.get(GameHandler.getState()));
    }

    public static boolean release() {
        if (pressed != null) {
            pressed.release();
            pressed = null;
            return true;
        }
        return false;
    }

    public static void update() {
        for (IElement element : new ArrayList<>(elements)) {
            if (element instanceof GButton) {

            }
        }
        if (GameHandler.getState() == GameState.lobby) {
            playerList.refresh();
        }
    }

    public static ArrayList<IElement> getElements() {
        return elements;
    }

    public static void popup(String message) {

    }

    private static void savePreset(GameState state) {
        menuPresets.put(state, new ArrayList<>(elements));
        elements.clear();
    }
}
