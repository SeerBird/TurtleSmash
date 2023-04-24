package seerbird.game.connection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class TurtleServer {
    Server server;

    public TurtleServer() {
        server = new Server();
        Kryo kryo = server.getKryo();
        kryo.register(ClientPacket.class);
        kryo.register(ServerPacket.class);
        server.start();
        int attempts = 0;
        while (attempts < 6) {
            try {
                server.bind(5455);
                break;
            } catch (IOException e) {
                attempts++;
            }
        }
        if (attempts == 6) {
            throw new RuntimeException("Failed to bind to port");
        }
        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof ClientPacket request) {
                    System.out.println(request.message);

                    ServerPacket response = new ServerPacket("ba".concat(request.message));
                    response.message = "ba".concat(request.message);
                    connection.sendTCP(response);
                }
            }
        });
    }
}
