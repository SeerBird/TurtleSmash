package game.connection.handlers;

import game.connection.packets.containers.ServerStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.BindException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Logger;

public class ServerDiscoverer extends SimpleChannelInboundHandler<DatagramPacket> {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Map<InetAddress, ServerStatus> servers;

    public ServerDiscoverer(Map<InetAddress, ServerStatus> servers) {
        this.servers = servers;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        String message = msg.content().toString(CharsetUtil.UTF_8);
        try {
            ServerStatus status;
            InetAddress address = InetAddress.getByName(message.substring(0, message.indexOf("/")));
            if((status=servers.get(address))==null){
                status = new ServerStatus();
                status.address = address;
                servers.put(address, status);
            }
            String processed = message.substring(message.indexOf("/") + 1); //5455/bababoi
            status.port = Integer.parseInt(processed.substring(0, processed.indexOf("/"))); //
            status.message = processed.substring(processed.indexOf("/")+1);
            status.nanoTime = System.nanoTime();
        } catch (UnknownHostException e) {
            logger.severe("Unknown host in server status " + msg.content().toString(CharsetUtil.UTF_8));
            System.out.println(e.getMessage()); // wtf is this... could try either way oh well
        } catch (IndexOutOfBoundsException e) {
            logger.severe("Malformed packet: " + msg.content().toString(CharsetUtil.UTF_8));
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
