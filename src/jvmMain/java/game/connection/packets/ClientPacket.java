package game.connection.packets;

import game.input.InputInfo;

public class ClientPacket extends Packet {
    static final long serialVersionUID = 32L;
    InputInfo input;
    public ClientPacket(InputInfo input){
        this.input=input;
    }
    public ClientPacket(){

    }

    public InputInfo getInput() {
        return input;
    }
}
