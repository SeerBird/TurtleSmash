package game.connection.packets.wrappers;

import game.input.InputInfo;

import java.io.Serial;
import java.io.Serializable;

public class ClientPacket implements Serializable {
    @Serial
    private static final long serialVersionUID = 80085;

    InputInfo input;
    public String name;
    public ClientPacket(InputInfo input, String name){
        this.input=input;
        this.name=name;
    }

    public InputInfo getInput() {
        return input;
    }
}
