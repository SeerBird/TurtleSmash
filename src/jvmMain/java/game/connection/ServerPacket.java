package game.connection;


public class ServerPacket extends Packet{
    String message;

    public ServerPacket(String message) {
        this.message = message;
    }

    public ServerPacket() {
        message = "";
    }
}
