package game.connection.packets;

import game.input.InputInfo;

public class ClientPacket extends Packet {
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
