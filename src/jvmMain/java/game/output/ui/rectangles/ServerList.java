package game.output.ui.rectangles;

import game.GameHandler;
import game.connection.packets.containers.ServerStatus;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerList extends RectElement {
    private final Map<InetAddress, ServerStatus> servers;
    public final HashMap<GButton, ServerStatus> buttons;
    private final GButton refresh;
    GButton pressed;

    public ServerList(double x, double y, int width, int height, Map<InetAddress, ServerStatus> servers) {
        super(x, y, width, height);
        this.servers = servers;
        buttons = new HashMap<>();
        pressed = null;
        refresh = new GButton(0,0,100,100, () -> refresh(),"Refresh");
    }

    @Override
    public boolean press(double x, double y) {
        for (GButton button : buttons.keySet()) {
            if (button.press(x, y)) {
                pressed = button;
                return true;
            }
        }
        if(refresh.press(x,y)){
            pressed = refresh;
            return true;
        }
        return false;
    }

    public void refresh() {
        GameHandler.refreshLAN();
        int buttonTestHeight = 40;
        buttons.clear();
        int buttCount = 0;
        for (InetAddress address : servers.keySet()) {
            GButton button = new GButton(x, y + buttCount * buttonTestHeight, width, buttonTestHeight,
                    null, servers.get(address).message);
            buttons.put(button, servers.get(address));
            button.setAction(() -> GameHandler.discoverToLobby(buttons.get(button)));
            buttCount++;
        }
    }

    @Override
    public void release() {//really shouldn't happen twice in a row... or before it got pressed the first time...
        pressed.release();
    }

    public ArrayList<GButton> getButtons() {
        ArrayList<GButton> res = new ArrayList<>(buttons.keySet());
        res.add(refresh);
        return res;
    }
}
