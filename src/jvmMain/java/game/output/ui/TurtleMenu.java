package game.output.ui;

import game.EventManager;
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
    private final ArrayList<IElement> elements;
    private final HashMap<GameState, ArrayList<IElement>> menuPresets;
    private IElement pressed;
    EventManager handler;
    //cringe element references
    final ServerList serverList;
    final PlayerList playerList;

    public TurtleMenu(EventManager handler, ServerPacket lastPacket, Map<InetAddress, ServerStatus> servers) {
        this.handler = handler;
        elements = new ArrayList<>();
        menuPresets = new HashMap<>();

        // Create the presets
        // main
        elements.add(new GButton(200, 200, 100, 100, handler::discover, "Discover"));// discover UDP
        elements.add(new GButton(400, 200, 100, 100, handler::host, "Host"));// host UDP and open TCP server
        menuPresets.put(GameState.main, new ArrayList<>(elements));
        elements.clear();
        //host
        elements.add(new GButton(400, 200, 100, 100, handler::playServer, "Play"));
        menuPresets.put(GameState.host, new ArrayList<>(elements));
        elements.clear();
        //connect
        serverList = new ServerList(this, 200, 200, 600, 600, servers);
        elements.add(serverList);
        menuPresets.put(GameState.discover, new ArrayList<>(elements));
        elements.clear();
        //lobby
        playerList = new PlayerList(100, 100, 600, 600, lastPacket);
        elements.add(playerList);
        menuPresets.put(GameState.lobby, new ArrayList<>(elements));
        //playServer
        menuPresets.put(GameState.playServer, new ArrayList<>(elements));
        //playClient
        menuPresets.put(GameState.playClient, new ArrayList<>(elements));
        refreshGameState();
    }

    public void press(ArrayRealVector pos) {
        for (IElement element : elements) {
            if (element.press(pos)) {
                pressed = element;
                break;
            }
        }
    }

    public GameState getState() {
        return handler.getState();
    }

    public void refreshGameState() {
        elements.clear();
        elements.addAll(menuPresets.get(handler.getState()));
    }

    public void release() {
        if (pressed != null) {
            pressed.release();
        }
    }

    public void update() {
        for (IElement element : new ArrayList<>(elements)) {
            if (element instanceof GButton) {

            }
        }
        if (handler.getState() == GameState.lobby) {
            playerList.refresh();
        }
    }

    public ArrayList<IElement> getElements() {
        return elements;
    }

    public void popup(String message) {

    }

    public EventManager getHandler() {
        return handler;
    }
}
