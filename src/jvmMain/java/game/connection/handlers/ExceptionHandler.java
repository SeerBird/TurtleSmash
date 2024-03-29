package game.connection.handlers;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.ConnectException;
import java.net.SocketAddress;
import java.util.logging.Logger;

public class ExceptionHandler extends ChannelDuplexHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        try{
            super.connect(ctx, remoteAddress, localAddress, promise);
        }catch(ConnectException e){
            logger.warning("Failed to connect to server: "+e.getMessage());
            //GameHandler.escape();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning(cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
    // ... override more outbound methods to "handle" their exceptions as well
}
