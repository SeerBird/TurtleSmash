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
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static public void setup() throws IOException, URISyntaxException {
        //get log path
        String os = System.getenv("OS");
        String pattern = "\\TurtleSmash\\logs\\";//remove TurtleSmash for release?
        URI uri;
        Path path;
        if (Objects.equals(os, "Windows_NT")) {
            pattern = (System.getenv("LOCALAPPDATA") + pattern).replaceAll("\\\\", "/");
            uri=URI.create("file:/"+pattern.substring(0, pattern.length() - 1));
            path=Paths.get(uri);
            Files.createDirectories(path);
        } /*else if (Objects.equals(os, "MacOS")) {

        } else if (Objects.equals(os, "Linux")) {

        } */else{
            pattern=System.getProperty("user.dir");
            uri=URI.create("file:/"+pattern);
            path=Paths.get(uri);
        }

        //get my logger
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        FileHandler fileTxt = new FileHandler(pattern + "Turtle%u.%g.txt",true);
        ConsoleHandler console = new ConsoleHandler();
        GFormatter formatter = new GFormatter(path);
        fileTxt.setFormatter(formatter);
        console.setFormatter(formatter);
        logger.addHandler(fileTxt);
        logger.addHandler(console);

        //silence default console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }
    }
}

class GFormatter extends Formatter {
    // this method is called for every log records
    String path;
    public GFormatter(Path path){
        this.path=path.toString();
    }
    public String format(@NotNull LogRecord rec) {
        return calcDate(rec.getMillis()) +
                ": " +
                formatMessage(rec) +
                "\n";
    }

    @NotNull
    private String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MM.dd HH:mm ssSSS");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

    // this method is called just after the handler using this
    // formatter is created
    public String getHead(Handler h) {
        return "Logger 9000 activated at "+path+"\n";
    }

    // this method is called just after the handler using this
    // formatter is closed
    public String getTail(Handler h) {
        return "Logger 9000 off\n\n";
    }
}
