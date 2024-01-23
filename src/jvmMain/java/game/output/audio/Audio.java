package game.output.audio;


import game.Resources;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static game.output.audio.Sound.*;


public class Audio {
    static final Map<Sound, URL> soundStreams = new HashMap<>();
    static Clip silent;
    static final Map<Sound, Clip> cooldownSounds = new HashMap<>();

    static {
        try {
            silent = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        soundStreams.put(death, Resources.goodnight);
        soundStreams.put(button, Resources.vine);
        soundStreams.put(webThrow, Resources.pew);
        soundStreams.put(collision, Resources.pipe);
    }

    public static void playSound(Sound sound) {// design some kind of notifiable object to stop the clip
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundStreams.get(sound)));
            autoClose(clip);
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void playCooldownSound(Sound sound) {// design some kind of notifiable object to stop the clip
        if (cooldownSounds.get(sound) != null) {
            if (cooldownSounds.get(sound).getFramePosition() < 20000 && cooldownSounds.get(sound).isActive()) {
                return;
            }
        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundStreams.get(sound)));
            autoClose(clip);
            clip.start();
            cooldownSounds.put(sound, clip);
        } catch (LineUnavailableException | UnsupportedAudioFileException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void autoClose(@NotNull Clip clip) {
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP)
                clip.close();
        });
    }
}
