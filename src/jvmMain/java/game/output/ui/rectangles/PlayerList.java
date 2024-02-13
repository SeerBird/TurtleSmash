package game.output.ui.rectangles;

import game.GameHandler;
import game.Player;
import game.util.DevConfig;

import java.util.ArrayList;

public class PlayerList extends RectElement {
    ArrayList<Label> labels;

    public PlayerList(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.labels = new ArrayList<>();
    }

    @Override
    public void release() {

    }

    static int labelHeight = 30;

    public void refresh() {
        labels.clear();
        labels.add(new Label(x, y * labelHeight, width, labelHeight,
                "Player List", DevConfig.shell));
        int counter = 1;
        for (Player player : GameHandler.getPlayers()) {
            labels.add(new Label(x, y + counter * labelHeight, width, labelHeight,
                    player.getName(), DevConfig.turtle));
            counter++;
        }
    }

    public ArrayList<Label> getLabels() {
        return labels;
    }
}
