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
        for (Class _class : Packet.usedClasses) {
            kryo.register(_class);
        }
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof ServerPacket response) {
                    handler.setWorld(response.world);
                }
            }
        });
    }

    public void connect(InetAddress server) {//threadify
        client.start();
        try {
            client.connect(5000, server, Config.TCPPort, Config.UDPPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void send(InputInfo input) {
        client.sendTCP(new ClientPacket(input));//disconnect if server disconnects?
    }

    private void discoverHosts() {
        LANServers = (ArrayList<InetAddress>) client.discoverHosts(Config.UDPPort, 500);
    }

    public void startDiscoveringHosts() {
        new Thread(this::discoverHosts).start();
    }

    public ArrayList<InetAddress> getHosts() {//copy?
        return LANServers;
    }

    public void resetHosts() {
        LANServers = null;
    }
}
