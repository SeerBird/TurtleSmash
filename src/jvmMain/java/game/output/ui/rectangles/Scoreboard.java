package game.output.ui.rectangles;

import game.GameHandler;
import game.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scoreboard extends RectElement {
    Map<Label, Label> playerScores;
    public boolean visible;

    public Scoreboard(double x, double y, int width, int height) {
        super(x, y, width, height);
        playerScores = new HashMap<>();
        refresh();
    }

    public void refresh() {
        playerScores.clear();
        ArrayList<Player> players = GameHandler.getPlayers();
        int playerWidth = 100;
        int rowHeight = 40;
        int scoreWidth = 100;
        int count = 0;
        for (Player player : players) {
            playerScores.put(
                    new Label(x, y + count * rowHeight, playerWidth, rowHeight, player.getName()), //add a ":"? look at how this is displayed
                    new Label(x + playerWidth, y + count * rowHeight, scoreWidth, rowHeight, String.valueOf(player.getScore())));
            count++;
        }
    }

    @Override
    public boolean press(double x, double y) {
        return false;
    }

    @Override
    public void release() {

    }

    public Map<Label,Label> getScores() {
        return playerScores;
    }
}
