package game.connection.packets.containers;

import game.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.ArrayList;

public class LobbyData {
    public ArrayList<InetAddress> players;

    public LobbyData(@NotNull ArrayList<Player> players) {
        this.players=new ArrayList<>();
        for(Player p:players){
            if(p.getChannel()!=null){
                this.players.add(p.getChannel().remoteAddress().getAddress());
            }
        }
    }
}
