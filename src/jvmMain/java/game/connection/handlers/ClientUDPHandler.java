package game.connection.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class ClientUDPHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    Map<String, Long> servers;

    public ClientUDPHandler(Map<String, Long> servers) {
        this.servers = servers;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        servers.put(msg.content().toString(CharsetUtil.UTF_8), System.nanoTime());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
