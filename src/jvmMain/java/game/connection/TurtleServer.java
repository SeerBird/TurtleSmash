package game.connection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import game.EventManager;

import java.io.IOException;

public class TurtleServer {
    Server server;
    EventManager handler;

    public TurtleServer(EventManager handler) {
        this.handler = handler;
        server = new Server(9999999,9999999);
        Kryo kryo = server.getKryo();
        kryo.register(ClientPacket.class);
        kryo.register(ServerPacket.class);
        server.start();
        try {
            server.bind(5455);
        } catch (IOException e) {
            throw new RuntimeException("Failed to bind to port");
        }
        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof ClientPacket request) {
                    System.out.println(request.message);

                    ServerPacket response = new ServerPacket("ba".concat(request.message));
                    connection.sendTCP(response);
                }
            }
        });
    }
}
