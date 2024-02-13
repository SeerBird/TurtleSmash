package game.connection.packets;

import game.input.InputInfo;

public class ClientPacket{
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
