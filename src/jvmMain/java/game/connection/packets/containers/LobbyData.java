package game.connection.packets.containers;

import game.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LobbyData {
    public LinkedHashMap<String, Integer> players;

    public LobbyData(@NotNull ArrayList<Player> playerList, Player recipient) {
        this.players = new LinkedHashMap<>();
        for (Player p : playerList) {
            if (p == recipient) {
                players.put("", p.getScore());
            } else {
                players.put(p.getName(), p.getScore());
            }
        }
    }
}
