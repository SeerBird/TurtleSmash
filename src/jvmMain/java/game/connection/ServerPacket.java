package game.connection;


public class ServerPacket {
    String message;

    public ServerPacket(String message) {
        this.message = message;
    }

    public ServerPacket() {
        message = "";
    }
}
