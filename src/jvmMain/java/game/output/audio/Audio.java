package game.output.audio;


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

    static {
        try {
            silent = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        soundStreams.put(death, Audio.class.getResource("goodnight.wav"));
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

    private static void autoClose(@NotNull Clip clip) {
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP)
                clip.close();
        });
    }
}
