package game.connection.packets.wrappers;

import game.connection.packets.messages.ClientMessage;
import game.input.InputInfo;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class ClientPacket implements Serializable {
    @Serial
    private static final long serialVersionUID = 80085;

    InputInfo input;
    public String name;

    public ClientPacket(InputInfo input, String name) {
        this.input = input;
        this.name = name;
    }

    public ClientPacket(@NotNull ClientMessage message) {
        input = new InputInfo(message.getInput());
        name = message.getName();
    }

    public game.connection.packets.messages.ClientMessage getMessage() {
        ClientMessage.Builder builder = ClientMessage.newBuilder()
                .setName(name)
                .setInput(input.getMessage());
        return builder.build();
    }

    public InputInfo getInput() {
        return input;
    }
}
