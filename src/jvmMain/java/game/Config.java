package game;

import java.util.prefs.Preferences;

public class Config {
    private static final String nameKey = "playerName";
    private static final Preferences prefs = Preferences.userRoot().node(Config.class.getName());

    public static String getName() {
        return prefs.get(nameKey, "creature");
    }
}
