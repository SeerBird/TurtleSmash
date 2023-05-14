package game.connection.packets.containers;

import java.net.InetAddress;

public class ServerStatus {
    public InetAddress address;
    public int port;
    public long nanoTime;
    public String message;

    public ServerStatus(){
    }
}
