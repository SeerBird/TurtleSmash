package game.connection;

public class ClientPacket extends Packet{
    String message;
    public ClientPacket(String message){
        this.message=message;
    }
    public ClientPacket(){
        message="";
    }
}
