package game.connection.packets.wrappers.containers;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 800852;
    public InetAddress address;
    public int port;
    public long nanoTime;
    public String message;
    public ServerStatus(InetAddress address, int port, String message){
        this.address=address;
        this.port=port;
        this.message=message;
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
