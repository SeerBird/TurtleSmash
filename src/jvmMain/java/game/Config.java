package game;

import game.util.Util;

import java.io.*;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

public class Config {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String nameKey = "playerName";
    private static final String fileName = "Config.xml";
    private static final Preferences prefs = Preferences.userRoot().node(Config.class.getName());

    static {
        try {
            Preferences.importPreferences(new FileInputStream(Util.path + fileName));
        } catch (IOException | InvalidPreferencesFormatException e) {
            logger.warning("Failed to import config, creating default");
            prefs.put(nameKey, "creature");
        }
    }

    public static String getName() {
        System.out.println();
        return prefs.get(nameKey, "creature");
    }

    public static void savePrefs() {
        try {
            OutputStream osTree = new BufferedOutputStream(new FileOutputStream(Util.path + fileName));
            prefs.exportSubtree(osTree);
            osTree.close();
            logger.info("Config saved");
        } catch (IOException | BackingStoreException e) {
            logger.severe("Failed to save the config file");
        }
    }
}
