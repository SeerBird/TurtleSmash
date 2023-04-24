package seerbird.game.connection;

import com.esotericsoftware.kryonet.Server;

public class ServerPacket {
    String message;

    public ServerPacket(String message) {
        this.message = message;
    }

    public ServerPacket() {
        message = "";
    }
}
