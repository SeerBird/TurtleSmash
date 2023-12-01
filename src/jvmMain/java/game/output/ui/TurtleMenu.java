package game.output.ui;

import game.Config;
import game.GameHandler;
import game.GameState;
import game.connection.Broadcaster;
import game.output.ui.rectangles.*;
import game.output.ui.rectangles.Label;
import game.util.DevConfig;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.awt.*;
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
    static final ServerList serverList = new ServerList(DevConfig.WIDTH / 4, 0, DevConfig.WIDTH / 2, DevConfig.HEIGHT);
    static final PlayerList playerList = new PlayerList(100, 100, 600, 600);
    static final Toggleable lobbyWaiting = new Toggleable(0, 0, 100, 100, "Wait for game start", Color.MAGENTA);
    static final Scoreboard scoreBoard = new Scoreboard(200, 200, 900, 200);

    static {
        //region create the presets for all the game states
        //region main
        savePreset(GameState.main,
                new GButton(DevConfig.WIDTH / 2 - 160, 200, 150, 150, GameHandler::mainToDiscover, "Discover", DevConfig.shell),
                new GButton(DevConfig.WIDTH / 2 + 10, 200, 150, 150, GameHandler::mainToHost, "Host", DevConfig.shell),
                new Label(DevConfig.WIDTH / 2 - 160, 360, 320, 40, "Choose Your Name!", DevConfig.turtle),
                new Textbox(DevConfig.WIDTH / 2 - 160, 400, 320, 40, Config.getPlayerName(), (text) -> Config.setName(text), DevConfig.turtle));
        //endregion
        //region host
        savePreset(GameState.host,
                new GButton(DevConfig.WIDTH/2-75, 200, 150, 150, GameHandler::hostToPlayServer, "Play", DevConfig.shell),
                new Textbox(DevConfig.WIDTH/2-75, 370, 150, 40, Config.getServerName(), (serverName) -> {
                    Config.setServerName(serverName);
                    Broadcaster.setMessage(Config.getServerName());
                }, DevConfig.turtle));
        //endregion
        //region connect
        savePreset(GameState.discover,
                serverList);
        //endregion
        //region lobby
        savePreset(GameState.lobby,
                playerList,
                lobbyWaiting);
        //endregion
        //region playServer
        savePreset(GameState.playServer, scoreBoard);
        //endregion
        //region playClient
        savePreset(GameState.playClient, scoreBoard);
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

    public static Focusable getFocused() {
        return focused;
    }

    public static void refreshServerList() {
        serverList.refresh();
    }

    public static void refreshScores() {
        scoreBoard.refresh();
    }

    public static void showScores() {
        scoreBoard.visible = true;
    }

    public static void hideScores() {
        scoreBoard.visible = false;
    }

    public static boolean lobbyWaiting() {
        return lobbyWaiting.getState();
    }

    public static void toggleLobbyWaiting() {
        lobbyWaiting.toggle();
    }

    public static IElement getPressed() {
        return pressed;
    }
}
