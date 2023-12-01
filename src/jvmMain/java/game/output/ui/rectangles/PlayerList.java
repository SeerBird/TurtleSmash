package game.output.ui.rectangles;

import game.GameHandler;
import game.connection.packets.ServerPacket;
import game.util.DevConfig;

import java.util.ArrayList;
import java.util.Objects;

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
        ServerPacket packet = GameHandler.getPacket();
        if (packet.changed) {
            labels.clear();
            int counter = 0;
            for (String name : packet.lobby.players) {
                labels.add(new Label(x, y + counter * labelHeight, width, labelHeight,
                        Objects.requireNonNullElse(name, "You, the idiot"), DevConfig.turtle));
                counter++;
            }
        }
    }

    public ArrayList<Label> getLabels() {
        return labels;
    }
}
