package game.connection.handlers;

import game.GameHandler;
import io.netty.channel.*;

import java.net.ConnectException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;

public class ExceptionHandler extends ChannelDuplexHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        try{
            super.connect(ctx, remoteAddress, localAddress, promise);
        }catch(ConnectException e){
            logger.warning("Failed to connect to server: "+e.getMessage());
            GameHandler.escape();
        }
    }
    // ... override more outbound methods to "handle" their exceptions as well
}
