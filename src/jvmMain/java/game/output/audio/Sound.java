package game.output.audio;

import game.connection.packets.messages.ServerMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Sound {
    death,
    button,
    webThrow,
    collision;

    @NotNull
    @Contract(pure = true)
    public ServerMessage.Sound getMessage() {
        switch (this) {
            case death -> {
                return ServerMessage.Sound.death;
            }
            case button -> {
                return ServerMessage.Sound.button;
            }
            case webThrow -> {
                return ServerMessage.Sound.webThrow;
            }
            case collision -> {
                return ServerMessage.Sound.collision;
            }
        }
        return ServerMessage.Sound.death;
    }
    @Nullable
    @Contract(pure = true)
    public static Sound getSoundFromMessage(@NotNull ServerMessage.Sound message){
        switch (message){
            case death -> {return death;}
            case button -> {return button;}
            case webThrow -> {return webThrow;}
            case collision -> {return collision;}
        }
        return null;
    }
}
