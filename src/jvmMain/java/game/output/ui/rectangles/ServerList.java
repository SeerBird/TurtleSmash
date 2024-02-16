package game.output.ui.rectangles;

import game.GameHandler;
import game.connection.packets.wrappers.containers.ServerStatus;
import game.output.Renderer;
import game.util.DevConfig;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerList extends RectElement {
    public final HashMap<Button, ServerStatus> buttonServers;
    Button pressed;
    public Label title;

    public ServerList(int x, int y, int width, int height) {
        super(x, y, width, height);
        buttonServers = new HashMap<>();
        pressed = null;
        title = new Label(x + width / 2 - Renderer.getStringWidth("Discovered Servers") / 2, y,
                Renderer.getStringWidth("Discovered Servers"), 40,
                "Discovered Servers", DevConfig.turtle);
    }

    @Override
    public boolean press(double x, double y) {
        for (Button button : buttonServers.keySet()) {
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
        int buttCount = 1;
        Map<InetAddress, ServerStatus> servers = GameHandler.getServers();
        for (InetAddress address : servers.keySet()) {
            Button button = new Button(x, y + buttCount * buttonTestHeight, width, buttonTestHeight,
                    null, servers.get(address).message, DevConfig.shell);
            buttonServers.put(button, servers.get(address));
            ServerStatus status = servers.get(address);
            button.setAction(() -> GameHandler.discoverToLobby(status));
            buttCount++;
        }
    }

    @Override
    public void release() {//really shouldn't happen twice in a row... or before it got pressed the first time...
        pressed.release();
        pressed = null;
    }

    public ArrayList<Button> getButtonServers() {
        return new ArrayList<>(buttonServers.keySet());
    }
}
