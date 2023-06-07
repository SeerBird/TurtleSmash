package game.connection.handlers;

import game.GameHandler;
import io.netty.channel.*;

import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;

public class ExceptionHandler extends ChannelDuplexHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    GameHandler handler;

    public ExceptionHandler(GameHandler handler) {
        this.handler = handler;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException) {
            logger.warning(cause.getMessage());
        }
    }


    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        ctx.connect(remoteAddress, localAddress, promise.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                logger.warning(future.cause().getMessage());
            }
        }));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                // Handle write exception here...
                Throwable failureCause = future.cause();
                logger.warning(failureCause.getMessage());
            }
        }));
    }

    // ... override more outbound methods to handle their exceptions as well
}
