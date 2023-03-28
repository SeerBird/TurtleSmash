package seerbird.game.output.connection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.*;

public class Connector {
    Client client;

    public Connector(){
        client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(SomeRequest.class);
        kryo.register(SomeResponse.class);
        client.start();
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof SomeResponse response) {
                    System.out.println(response.text);
                }
            }
        });

    }

    public void connect(String host, int tcpPort, int udpPort) throws IOException {
        client.connect(5000, host, tcpPort, udpPort);
    }

    public void send() {
        SomeRequest request = new SomeRequest();
        request.text = "Here is the request";
        client.sendTCP(request);
    }
}
