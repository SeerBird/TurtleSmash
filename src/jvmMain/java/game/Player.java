package game;

import com.esotericsoftware.kryonet.Connection;
import game.connection.ClientPacket;
import game.connection.InputInfo;
import game.world.bodies.Body;
import game.world.bodies.Box;
import game.world.bodies.Turtle;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class Player {
    Box body; //should be turtle
    EventManager handler;
    InputInfo input;
    Connection connection;

    public Player(EventManager handler) {
        this.handler = handler;
        input = new InputInfo();
        body = new Box(handler.getWorld(), handler.getMousepos(), new ArrayRealVector(new Double[]{40.0, 0.0}), new ArrayRealVector(new Double[]{0.0, 40.0}));
    }

    public void setBody(Box body) {
        this.body = body;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
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

    public Body getBody() {
        return body;
    }
}
