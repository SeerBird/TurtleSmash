package game.connection;

import com.google.gson.Gson;
import game.EventManager;
import game.connection.handlers.ClientDecoder;
import game.connection.handlers.ClientTcpHandler;
import game.connection.handlers.ExceptionHandler;
import game.connection.packets.ClientPacket;
import game.connection.packets.containers.ServerStatus;
import game.input.InputInfo;
import game.util.Multiplayer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;
import java.net.InetAddress;
import java.security.cert.CertificateException;
import java.util.ArrayList;

public class ClientTCP extends Thread {
    EventManager handler;
    Gson gson;
    Channel channel;
    ServerStatus target;

    public ClientTCP(EventManager handler, ServerStatus target) {
        this.handler = handler;
        this.target = target;
        gson=new Gson();
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        Runtime.getRuntime().addShutdownHook(new Thread(group::shutdownGracefully));
        final SslContext sslCtx;
        try {
            sslCtx = Multiplayer.buildSslContext();
        } catch (CertificateException | SSLException e) {
            throw new RuntimeException(e);
        }
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), Multiplayer.localhost, 5445));
                            }
                            p.addLast(
                                    new ObjectEncoder(),
                                    new ClientDecoder(1048576, ClassResolvers.cacheDisabled(null)),
                                    new ClientTcpHandler(handler),
                                    new ExceptionHandler(handler)
                            );
                        }
                    });
            try {
                channel = b.connect(target.address, target.port).sync().channel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            handler.disconnectClient();
            group.shutdownGracefully();
        }
    }

    public void send(InputInfo input) {
        if(channel!=null){
        channel.writeAndFlush(gson.toJson(new ClientPacket(input)));}
    }
}
