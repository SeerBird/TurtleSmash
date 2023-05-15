package game.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static public void setup() throws IOException {
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.INFO);
        FileHandler fileTxt = new FileHandler("Logging.txt");
        fileTxt.setFormatter(new SimpleFormatter());
        logger.addHandler(fileTxt);
        logger.info("Set up logging");
    }
}
