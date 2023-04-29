package game.connection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import game.Config;
import game.EventManager;
import game.world.World;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class TurtleClient {
    Client client;
    EventManager handler;
    Runnable discoverHosts;
    ArrayList<InetAddress> LANServers;

    public TurtleClient(EventManager handler) {
        this.handler = handler;
        LANServers = new ArrayList<>();
        client = new Client(99999999, 999999999);
        Kryo kryo = client.getKryo();
        kryo.register(ClientPacket.class);
        kryo.register(ServerPacket.class);
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof ServerPacket response) {
                    System.out.println(response.message);
                    send("ba".concat(response.message));
                }
            }
        });
    }

    public void connect(InetAddress server) {
        client.start();
        try {
            client.connect(5000, server, Config.TCPPort, Config.UDPPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String message) {
        client.sendTCP(new ClientPacket(message));
    }

    public void send(World world) {

    }
    private void discoverHosts(){
        LANServers = (ArrayList<InetAddress>) client.discoverHosts(Config.UDPPort, 5000);
    }

    public void startDiscoveringHosts() {
        new Thread(this::discoverHosts).start();
    }

    public ArrayList<InetAddress> getHosts() {//copy?
        return LANServers;
    }
}
