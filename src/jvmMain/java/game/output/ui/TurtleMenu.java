package game.output.ui;

import game.GameHandler;
import game.GameState;
import game.output.ui.rectangles.GButton;
import game.output.ui.rectangles.PlayerList;
import game.output.ui.rectangles.ServerList;
import game.output.ui.rectangles.Textbox;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class TurtleMenu {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final ArrayList<IElement> elements = new ArrayList<>();
    private static final HashMap<GameState, ArrayList<IElement>> menuPresets = new HashMap<>();
    private static IElement pressed;
    private static Focusable focused;
    static final ServerList serverList = new ServerList(200, 200, 600, 600, GameHandler.servers);
    static final PlayerList playerList = new PlayerList(100, 100, 600, 600, GameHandler.lastPacket);

    static {
        //region create the presets for all the game states
        //region main
        savePreset(GameState.main,
                new GButton(200, 200, 100, 100, GameHandler::mainToDiscover, "Discover"),
                new GButton(400, 200, 100, 100, GameHandler::mainToHost, "Host"));
        //endregion
        //region host
        savePreset(GameState.host,
                new GButton(400, 200, 100, 100, GameHandler::hostToPlayServer, "Play"));
        //endregion
        //region connect
        savePreset(GameState.discover,
                serverList);
        //endregion
        //region lobby
        savePreset(GameState.lobby,
                playerList);
        //endregion
        //region playServer
        savePreset(GameState.playServer);
        //endregion
        //region playClient
        savePreset(GameState.playClient);
        //endregion
        //endregion
        refreshGameState();
    }

    private static void savePreset(GameState state, IElement... presetElements) {
        menuPresets.put(state, new ArrayList<>(List.of(presetElements)));
        elements.clear();
    }

    public static void refreshGameState() {
        elements.clear();
        elements.addAll(menuPresets.get(GameHandler.getState()));
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

    public static boolean release() {
        if (pressed != null) {
            pressed.release();
            pressed = null;
            return true;
        }
        return false;
    }

    public static void focus(Focusable element) {
        focused = element;
    }

    public static void unfocus() { //I can make this multilevel. no need though.
        focused.leave();
        focused = null;
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

    public static boolean isFocused() {
        return focused!=null;
    }

    public static void refreshServerList() {
        serverList.refresh();
    }
}
