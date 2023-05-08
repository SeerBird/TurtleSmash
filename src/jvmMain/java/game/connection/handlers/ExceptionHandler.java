package game.connection.handlers;

import game.EventManager;
import io.netty.channel.*;

import java.net.SocketAddress;

public class ExceptionHandler extends ChannelDuplexHandler {
    EventManager handler;
    public ExceptionHandler(EventManager handler){
        this.handler=handler;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("I got a boo boo: " + cause.getMessage());
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        ctx.connect(remoteAddress, localAddress, promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    Throwable failureCause = future.cause();
                    System.out.println(failureCause.getMessage());
                }
            }
        }));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    // Handle write exception here...
                    Throwable failureCause = future.cause();
                }
            }
        }));
    }

    // ... override more outbound methods to handle their exceptions as well
}
