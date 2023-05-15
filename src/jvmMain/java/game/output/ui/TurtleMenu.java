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
        savePreset(GameState.main);
        //host
        elements.add(new GButton(400, 200, 100, 100, handler::playServer, "Play"));
        savePreset(GameState.host);
        //connect
        serverList = new ServerList(this, 200, 200, 600, 600, servers);
        elements.add(serverList);
        savePreset(GameState.discover);
        //lobby
        playerList = new PlayerList(100, 100, 600, 600, lastPacket);
        elements.add(playerList);
        savePreset(GameState.lobby);
        //playServer
        savePreset(GameState.playServer);
        //playClient
        savePreset(GameState.playClient);
        refreshGameState();
    }

    public boolean press(ArrayRealVector pos) {
        for (IElement element : elements) {
            if (element.press(pos)) {
                pressed = element;
                return true;
            }
        }
        return false;
    }

    public GameState getState() {
        return handler.getState();
    }

    public void refreshGameState() {
        elements.clear();
        elements.addAll(menuPresets.get(handler.getState()));
    }

    public boolean release() {
        if (pressed != null) {
            pressed.release();
            pressed=null;
            return true;
        }
        return false;
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
    private void savePreset(GameState state){
        menuPresets.put(state, new ArrayList<>(elements));
        elements.clear();
    }
}
