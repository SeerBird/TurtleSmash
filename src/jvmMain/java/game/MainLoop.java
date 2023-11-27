package game;

import game.util.Logging;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class MainLoop {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static boolean run;
    private final static Thread onShutdown = new Thread(() -> {
    });

    public static void run() {
        Runtime.getRuntime().addShutdownHook(onShutdown);
        try {
            Logging.setup();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("Failure creating the log files");
        }
        run = true;
        int minFrameTime = 1000000000 / 60; // nanoseconds
        long last = 0;
        long now;
        while (run) {
            now = System.nanoTime();
            long timeLeft = minFrameTime - now + last;
            if (timeLeft <= 0) { // if a time of a frame has passed, tick
                GameHandler.update();
                GameHandler.out();
                last = now;
            } else { // otherwise, sleep for the time left
                try {
                    Thread.sleep(timeLeft / 1001000);
                } catch (InterruptedException e) {
                    logger.info(e.getMessage());
                }
            }
        }
    }

    private void terminate() {
        run = false;
    }
}
