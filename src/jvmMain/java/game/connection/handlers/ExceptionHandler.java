package game.connection.handlers;

import game.EventManager;
import io.netty.channel.*;

import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;

public class ExceptionHandler extends ChannelDuplexHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    EventManager handler;
    public ExceptionHandler(EventManager handler){
        this.handler=handler;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(cause instanceof SocketException){
            logger.warning(cause.getMessage());
        }
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
                    System.out.println(failureCause.getMessage());
                }
            }
        }));
    }

    // ... override more outbound methods to handle their exceptions as well
}
