package game.connection.packets.containers;

import game.Player;

import java.net.InetAddress;
import java.util.ArrayList;

public class LobbyData {
    public ArrayList<InetAddress> players;

    public LobbyData(ArrayList<Player> players) {
        this.players=new ArrayList<>();
        for(Player p:players){
            if(p.getChannel()!=null){
                this.players.add(p.getChannel().remoteAddress().getAddress());
            }
        }
    }
}
