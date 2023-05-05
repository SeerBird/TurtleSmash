package game;

import com.esotericsoftware.kryonet.Connection;
import game.connection.ClientPacket;
import game.connection.InputInfo;
import game.connection.ServerPacket;
import game.world.bodies.Body;
import game.world.bodies.Box;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

public class Player {
    Box body; //should be turtle
    EventManager handler;
    InputInfo input;
    SocketChannel channel;

    public Player(@NotNull EventManager handler) {
        this.handler = handler;
        input = new InputInfo();
        body = new Box(handler.getWorld(), handler.getMousepos(), new ArrayRealVector(new Double[]{40.0, 0.0}), new ArrayRealVector(new Double[]{0.0, 40.0}));
    }

    public void setBody(Box body) {
        this.body = body;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public InputInfo getInput() {
        return input;
    }

    //Player Actions
    public void flingWeb(@NotNull ArrayRealVector target) {
        input.webFling = target.copy();
    }

    public void receive(@NotNull ClientPacket packet) {
        //unpack it here? seems harmless
        input = packet.getInput();
    }
    public void send(ServerPacket packet){
        channel.write(packet).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                channel.flush();
            }
        });
    }

    public Body getBody() {
        return body;
    }
}
