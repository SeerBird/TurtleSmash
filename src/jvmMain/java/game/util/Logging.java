package game.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Logging {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static public void setup() throws IOException {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        FileHandler fileTxt = new FileHandler("Logging.txt");
        ConsoleHandler console = new ConsoleHandler();
        fileTxt.setFormatter(new GFormatter());
        console.setFormatter(new GFormatter());
        logger.addHandler(fileTxt);
        logger.addHandler(console);
    }
}

class GFormatter extends Formatter {
    // this method is called for every log records
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
        return "Logger 9000 activated\n";
    }

    // this method is called just after the handler using this
    // formatter is closed
    public String getTail(Handler h) {
        return "Logger 9000 off";
    }
}
