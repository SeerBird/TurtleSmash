package game.connection.packets;

import game.input.InputInfo;

public class ClientPacket extends Packet {
    InputInfo input;
    public String name;
    public ClientPacket(InputInfo input, String name){
        this.input=input;
        this.name=name;
    }
    public ClientPacket(){

    }

    public InputInfo getInput() {
        return input;
    }
}
