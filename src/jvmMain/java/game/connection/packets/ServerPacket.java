package game.connection.packets;

import game.connection.packets.containers.LobbyData;
import game.connection.packets.containers.WorldData;
import org.jetbrains.annotations.NotNull;

public class ServerPacket extends Packet {
    public boolean playing;
    public WorldData world;
    public LobbyData lobby;
    public transient boolean changed;

    public ServerPacket() {
    }

    public void set(@NotNull ServerPacket packet) {
        this.world = packet.world;
        this.lobby = packet.lobby;
        this.playing=packet.playing;
        changed = true;
    }
}
