package seerbird.game.connection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class TurtleClient {
    Client client;

    public TurtleClient() {
        client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(ClientPacket.class);
        kryo.register(ServerPacket.class);
        client.start();
        try {
            client.connect(5000, "localhost", 5455);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof ServerPacket response) {
                    System.out.println(response.message);
                    send("ba".concat(response.message));
                }
            }
        });
    }

    public void send(String message) {
        client.sendTCP(new ClientPacket(message));
    }
}
