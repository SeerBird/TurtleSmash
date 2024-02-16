package game.connection.packets.wrappers;

import game.connection.packets.messages.ServerMessage;
import game.connection.packets.wrappers.containers.LobbyData;
import game.connection.packets.wrappers.containers.WorldData;
import game.connection.packets.wrappers.containers.images.animations.AnimationImage;
import game.output.audio.Sound;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class ServerPacket implements Serializable {
    @Serial
    private static final long serialVersionUID = 455;
    public boolean playing;
    public WorldData world;
    public LobbyData lobby;
    public transient boolean changed;
    public ArrayList<AnimationImage<?>> animationImages; //accumulated over time
    public ArrayList<Sound> sounds;

    public ServerPacket() {
        animationImages = new ArrayList<>();
        sounds = new ArrayList<>();
    }

    public ServerPacket(@NotNull ServerMessage message) {
        playing = message.getPlaying();
        world = new WorldData(message.getWorld());
        lobby = new LobbyData(message.getLobby());
        animationImages = new ArrayList<>();
        sounds = new ArrayList<>();
        for (ServerMessage.AnimationM animationMessage : message.getAnimationList()) {
            animationImages.add(AnimationImage.getImageFromMessage(animationMessage));
        }
        for (ServerMessage.Sound soundMessage : message.getSoundList()) {
            sounds.add(Sound.getSoundFromMessage(soundMessage));
        }
    }

    public ServerMessage getMessage() {
        ServerMessage.Builder builder = ServerMessage.newBuilder()
                .setPlaying(playing)
                .setWorld(world.getMessage())
                .setLobby(lobby.getMessage());
        for (AnimationImage<?> animation : animationImages) {
            builder.addAnimation(animation.getMessage());
        }
        for (Sound sound : sounds) {
            builder.addSound(sound.getMessage());
        }
        return builder.build();
    }

    public void set(@NotNull ServerPacket packet) {
        this.world = packet.world;
        this.lobby = packet.lobby;
        this.playing = packet.playing;
        this.animationImages = packet.animationImages;
        this.sounds = packet.sounds;
        changed = true;
    }

    public void clear() {
        if (animationImages != null) {
            animationImages.clear();
        }
        if (sounds != null) {
            sounds.clear();
        }
    }
}
