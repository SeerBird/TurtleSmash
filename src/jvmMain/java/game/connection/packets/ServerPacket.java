package game.connection.packets;


import game.connection.packets.Packet;
import game.world.World;
import org.jetbrains.annotations.NotNull;

public class ServerPacket extends Packet {
    static final long serialVersionUID = 47L;
    public World world;

    public ServerPacket(World world) {
        this.world=new World();
        this.world.set(world);
    }

    public ServerPacket() {
        this.world=new World();
    }

    public void set(@NotNull ServerPacket packet) {
        this.world=packet.world;
    }
}
