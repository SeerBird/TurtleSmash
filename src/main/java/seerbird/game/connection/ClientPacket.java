package seerbird.game.connection;

public class ClientPacket {
    String message;
    public ClientPacket(String message){
        this.message=message;
    }
    public ClientPacket(){
        message="";
    }
}
