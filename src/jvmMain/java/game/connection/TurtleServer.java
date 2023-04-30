package game.connection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import game.EventManager;
import game.Player;
import game.world.World;
import game.world.bodies.Box;

import java.io.IOException;
import java.util.ArrayList;

public class TurtleServer {
    Server server;
    EventManager handler;

    public TurtleServer(EventManager handler) {
        this.handler = handler;
        server = new Server(9999999, 9999999);
        Log.DEBUG();
        Kryo kryo = server.getKryo();
        for (Class _class : Packet.usedClasses) {
            kryo.register(_class);
        }
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                handler.addPlayer(connection);
            }
        });
        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ClientPacket) {
                    Player player;
                    if ((player = handler.getPlayer(connection)) != null) {
                        player.receive((ClientPacket) object);
                    }
                }
            }
        });
    }

    public void start() {
        server.start();
        try {
            server.bind(5455, 8828);
        } catch (IOException e) {
            throw new RuntimeException("Failed to bind to port");
        }
    }

    public void sendToAll(ServerPacket packet) {
        server.sendToAllTCP(packet);
    }

    public Connection[] getConnections() {
        return server.getConnections();
    }

}
