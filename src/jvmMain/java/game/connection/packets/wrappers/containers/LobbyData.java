package game.connection.packets.wrappers.containers;

import game.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LobbyData implements Serializable {
    @Serial
    private static final long serialVersionUID = 800851;
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
