package game.connection.packets;


import game.connection.packets.Packet;
import game.world.World;

public class ServerPacket extends Packet {
    World world;

    public ServerPacket(World world) {
        this.world = world;
    }

    public ServerPacket() {

    }
}
