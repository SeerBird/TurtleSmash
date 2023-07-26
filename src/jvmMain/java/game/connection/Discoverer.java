package game.connection;

import game.connection.packets.containers.ServerStatus;
import game.util.Multiplayer;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static game.util.Multiplayer.groupAddress;

public class Discoverer{
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected DatagramSocket socket;
    ScheduledFuture future;
    protected byte[] buf = new byte[256];

    public Discoverer(Map<InetAddress, ServerStatus> servers) {
        try {
            socket = new MulticastSocket(4445);
        } catch (IOException e) {
            logger.warning("Failed to create multicast discoverer socket");
        }
        SocketAddress group = new InetSocketAddress(groupAddress, 0);
        try {
            socket.joinGroup(group, Multiplayer.networkInterface);
            //socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP,false);
        } catch (IOException e) {
            logger.warning("Failed to join multicast group");
        }
        future = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                logger.warning("I/O exception receiving multicast packet");
            }
            String status = new String(
                    packet.getData(), 0, packet.getLength());
            ServerStatus received;
            try{
                received=new ServerStatus(status);
            }catch (UnknownHostException e) {
                logger.warning("Failed to assign received server IP to an address");
                return;
            }catch (IndexOutOfBoundsException e){
                logger.warning("Unknown format of received message");
                return;
            }
            servers.put(received.address,received);
        }, 0, 1, TimeUnit.MILLISECONDS);
        logger.info("Listening for servers on " + groupAddress.getHostAddress() + ':' + 4445
                + " on interface " + Multiplayer.networkInterface.getDisplayName());
    }
    public void stop(){
        if(future!=null){
            future.cancel(true);
        }
        if(socket!=null){
            try {
                socket.leaveGroup(new InetSocketAddress(groupAddress, 4445), Multiplayer.networkInterface);
            } catch (IOException e) {
                logger.warning("Error leaving group");
            }
            socket.close();
        }
    }
}
