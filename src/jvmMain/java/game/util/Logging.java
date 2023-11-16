package game.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.*;

public class Logging {
    public static void setup() throws IOException, URISyntaxException {

        Path path;
        String pattern = "/TurtleSmash/logs/";//remove TurtleSmash for windows?
        //region get log path
            String os = System.getProperty("os.name");
            URI uri;
            if (os.contains("Windows")) { //check separate versions
                pattern = (System.getenv("LOCALAPPDATA") + pattern).replaceAll("\\\\", "/");
                uri = URI.create("file:/" + pattern.substring(0, pattern.length() - 1));
                path = Paths.get(uri);
                Files.createDirectories(path);
            } else if (Objects.equals(os, "MacOS")) { //figure this out
                pattern = System.getProperty("user.dir");
                uri = URI.create("file:/" + pattern);
                path = Paths.get(uri);
            } else if (os.contains("Linux")) {
                pattern = System.getProperty("user.dir");
                uri = URI.create("file:///tmp/" + pattern); //find another path? tmp ain't nice
                path = Paths.get(uri);
            } else {
                pattern = System.getProperty("user.dir");
                uri = URI.create("file:/" + pattern);
                path = Paths.get(uri);
            }
        //endregion
        //region set up my logger
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        FileHandler fileTxt = new FileHandler(pattern + "Turtle%u.%g.txt", true);
        ConsoleHandler console = new ConsoleHandler();
        GFormatter formatter = new GFormatter(path);
        fileTxt.setFormatter(formatter);
        console.setFormatter(formatter);
        console.setLevel(Level.INFO);
        fileTxt.setLevel(Level.INFO);
        logger.addHandler(fileTxt);
        logger.addHandler(console);
        //endregion
        //region silence default console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }
        //endregion
    }
}

class GFormatter extends Formatter {
    private final String path;

    public GFormatter(@NotNull Path path) {
        this.path = path.toString();
    }

    public String format(@NotNull LogRecord rec) {
        return "(" + calcDate(rec.getMillis()) + ") "
                + rec.getSourceClassName() + ": " +
                formatMessage(rec) +
                "\n";
    }

    @NotNull
    private String calcDate(long millis) {
        //SimpleDateFormat date_format = new SimpleDateFormat("MM.dd HH:mm ssSSS");
        SimpleDateFormat date_format = new SimpleDateFormat("ssSSS");
        Date date = new Date(millis);
        return date_format.format(date);
    }

    public String getHead(Handler h) {
        return "Logger 9000 activated at " + path + "\n";
    }

    public String getTail(Handler h) {
        return "Logger 9000 off\n\n";
    }
}
