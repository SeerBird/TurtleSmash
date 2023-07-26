package game.connection.packets.containers;

import game.util.Multiplayer;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerStatus {
    public InetAddress address;
    public int port;
    public long nanoTime;
    public String message;

    public ServerStatus(){
    }
    public ServerStatus(@NotNull String message) throws UnknownHostException, IndexOutOfBoundsException {
        address = InetAddress.getByName(message.substring(0, message.indexOf("/")));
        String processed = message.substring(message.indexOf("/") + 1); //everything except the ip
        port = Integer.parseInt(processed.substring(0, processed.indexOf("/")));
        this.message = processed.substring(processed.indexOf("/")+1);
        nanoTime = System.nanoTime();
    }
    public String getStatus(){
        return address.getHostAddress() + "/" + port + "/" + message;
    }
}
