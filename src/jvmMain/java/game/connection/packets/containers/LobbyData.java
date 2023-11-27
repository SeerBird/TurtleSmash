package game.connection.packets.containers;

import game.GameHandler;
import game.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.ArrayList;

public class LobbyData {
    public ArrayList<String> players;

    public LobbyData(@NotNull ArrayList<Player> playerList, Player recipient) {
        this.players = new ArrayList<>();
        for (Player p : playerList) {
            if (p == recipient) {
                players.add(null);
            } else {
                players.add(p.getName());
            }
        }
    }
}
