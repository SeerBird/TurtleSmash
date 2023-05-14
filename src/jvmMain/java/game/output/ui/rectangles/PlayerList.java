package game.output.ui.rectangles;

import game.Player;
import game.connection.packets.ServerPacket;

import java.net.InetAddress;
import java.util.ArrayList;

public class PlayerList extends RectElement {
    ServerPacket packet;
    ArrayList<Label> labels;

    public PlayerList(double x, double y, int width, int height, ServerPacket packet) {
        super(x, y, width, height);
        this.packet = packet;
        this.labels=new ArrayList<>();
    }

    @Override
    public void release() {

    }

    static int labelHeight = 30;

    public void refresh() {
        if (packet.changed) {
            labels.clear();
            int counter = 0;
            for (InetAddress address : packet.lobby.players) {
                labels.add(new Label(x, y + counter * labelHeight, width, labelHeight, address.toString()));
                counter++;
            }
        }
    }

    public ArrayList<Label> getLabels() {
        return labels;
    }
}
