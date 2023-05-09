package game.connection.handlers;

import game.connection.ServerUDP;
import game.connection.packets.data.ServerStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.BindException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class ClientUDPHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Map<InetAddress, ServerStatus> servers;

    public ClientUDPHandler(Map<InetAddress, ServerStatus> servers) {
        this.servers = servers;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        String message = msg.content().toString(CharsetUtil.UTF_8);
        try {
            ServerStatus status = new ServerStatus();
            InetAddress address = InetAddress.getByName(message.substring(0, message.indexOf("/")));
            status.address = address;
            String processed = message.substring(message.indexOf("/") + 1);
            status.port = Integer.parseInt(processed.substring(0, processed.indexOf("/")));
            status.nanoTime = System.nanoTime();
            servers.put(address, status);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host in server status " + msg.content().toString(CharsetUtil.UTF_8));
            System.out.println(e.getMessage()); // wtf is this... could try either way oh well
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Malformed packet: " + msg.content().toString(CharsetUtil.UTF_8));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof BindException){
            logger.warning(cause.getMessage());
        }else{
            super.exceptionCaught(ctx,cause);
        }
    }
}
