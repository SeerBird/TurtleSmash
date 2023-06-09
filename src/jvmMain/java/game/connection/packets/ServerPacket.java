package game.connection.packets;
import game.Player;
import game.connection.packets.containers.LobbyData;
import game.connection.packets.containers.WorldData;
import game.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ServerPacket extends Packet {
    public WorldData world;
    public LobbyData lobby;
    public transient boolean changed;

    public ServerPacket(World world, ArrayList<Player> players) {
        this.world=new WorldData(world);
        lobby=new LobbyData(players);
    }

    public ServerPacket() {
    }
    public void set(@NotNull ServerPacket packet) {
        this.world=packet.world;
        this.lobby=packet.lobby;
        changed=true;
    }
}
