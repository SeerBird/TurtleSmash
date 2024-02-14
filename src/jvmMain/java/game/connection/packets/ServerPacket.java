package game.connection.packets;

import game.connection.packets.containers.LobbyData;
import game.connection.packets.containers.WorldData;
import game.connection.packets.containers.images.animations.AnimationImage;
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
