package game.connection.packets;


import game.connection.packets.Packet;
import game.connection.packets.data.WorldData;
import game.world.World;
import game.world.bodies.Body;
import org.jetbrains.annotations.NotNull;

public class ServerPacket extends Packet {
    public WorldData world;

    public ServerPacket(World world) {
        this.world=new WorldData(world);
    }

    public ServerPacket() {
    }

    public void set(@NotNull ServerPacket packet) {
        this.world=packet.world;
    }
}
