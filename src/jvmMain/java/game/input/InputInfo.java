package game.input;

import game.connection.packets.messages.ClientMessage;
import game.connection.packets.wrappers.containers.images.ArrayRealVectorImage;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.Serial;
import java.io.Serializable;

public class InputInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 4550;
    public ArrayRealVector mousepos;
    public boolean teleport = false;
    public boolean create = false;
    public boolean webFling = false;
    public boolean detachWeb = false;

    public InputInfo() {
    }

    public InputInfo(ClientMessage.InputM input) {
        mousepos = ArrayRealVectorImage.getVector(input.getMousepos());
        teleport = input.getTeleport();
        create = input.getCreate();
        webFling = input.getWebFling();
        detachWeb = input.getDetachWeb();
    }

    public ClientMessage.InputM getMessage() {
        return ClientMessage.InputM.newBuilder()
                .setCreate(create)
                .setDetachWeb(detachWeb)
                .setTeleport(teleport)
                .setMousepos(ArrayRealVectorImage.getMessage(mousepos))
                .setWebFling(webFling)
                .build();
    }

    public void create() {
        create = true;
    }

    public void drag() {
        teleport = true;
    }

    public void webFling() {
        webFling = true;
    }

    public void reset() {
        teleport = false;
        create = false;
        webFling = false;
        detachWeb = false;
    }

    public void detachWeb() {
        detachWeb = true;
    }
}
