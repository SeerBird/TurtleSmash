package game.connection;

import game.EventManager;
import game.util.Connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class TurtleClient {
    EventManager handler;
    ArrayList<InetAddress> LANServers;

    public TurtleClient(EventManager handler) {
        this.handler = handler;
        LANServers = new ArrayList<>();
    }

    public void connect(InetAddress server) {//threadify
    }

    public void send(InputInfo input) {
        //disconnect if server disconnects?
    }

    public void discoverHosts() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] buffer = Connection.localIp.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, Connection.broadcastIP, 4445);
            socket.send(packet);
            socket.close();
        } catch (IOException ignored) {
            //might want to use this at some point
        }
    }

    public ArrayList<InetAddress> getHosts() {//copy?
        return LANServers;
    }

    public void resetHosts() {
        LANServers = null;
    }
}
