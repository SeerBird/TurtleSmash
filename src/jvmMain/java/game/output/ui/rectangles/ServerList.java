package game.output.ui.rectangles;

import game.GameHandler;
import game.connection.packets.containers.ServerStatus;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerList extends RectElement {
    public final HashMap<GButton, ServerStatus> buttonServers;
    GButton pressed;

    public ServerList(double x, double y, int width, int height) {
        super(x, y, width, height);
        buttonServers = new HashMap<>();
        pressed = null;
    }

    @Override
    public boolean press(double x, double y) {
        for (GButton button : buttonServers.keySet()) {
            if (button.press(x, y)) {
                pressed = button;
                return true;
            }
        }
        return false;
    }

    public void refresh() {
        int buttonTestHeight = 40;
        buttonServers.clear();
        int buttCount = 0;
        Map<InetAddress, ServerStatus> servers= GameHandler.getServers();
        for (InetAddress address : servers.keySet()) {
            GButton button = new GButton(x, y + buttCount * buttonTestHeight, width, buttonTestHeight,
                    null, servers.get(address).message);
            buttonServers.put(button, servers.get(address));
            button.setAction(() -> GameHandler.discoverToLobby(buttonServers.get(button)));
            buttCount++;
        }
    }

    @Override
    public void release() {//really shouldn't happen twice in a row... or before it got pressed the first time...
        pressed.release();
    }

    public ArrayList<GButton> getButtonServers() {
        return new ArrayList<>(buttonServers.keySet());
    }
}
