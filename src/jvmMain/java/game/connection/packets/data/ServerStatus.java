package game.connection.packets.data;

import java.net.InetAddress;

public class ServerStatus {
    public InetAddress address;
    public int port;
    public long nanoTime;
    public ServerStatus(){
    }
}
