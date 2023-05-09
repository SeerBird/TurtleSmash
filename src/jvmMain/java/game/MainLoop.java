package game;

import game.util.CONSTANTS;
import game.util.Logging;

import java.io.IOException;
import java.util.logging.Logger;

public class MainLoop {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static boolean run;
    private final static Thread onShutdown = new Thread(new Runnable(){
        @Override
        public void run() {
        }
    });

    public static void run() {
        Runtime.getRuntime().addShutdownHook(onShutdown);
        try {
            Logging.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
        run = true;
        int minFrameTime = 1000000000 / CONSTANTS.MAX_FRAMERATE; // nano
        EventManager manager = new EventManager();
        boolean outJob = false;
        long last = 0;
        long now;
        int updateJobs = 0;
        while (run) {
            now = System.nanoTime();
            long timeLeft = minFrameTime - now + last;
            if (timeLeft <= 0) {
                updateJobs++;
            } else{
                try {
                    Thread.sleep(timeLeft/1001000);
                } catch (InterruptedException e) {
                    logger.info(e.getMessage());
                }
            }
            if (updateJobs > 0) {
                manager.update();
                outJob = true;
                updateJobs--;
                last = now;
            }
            if (outJob) {
                manager.out();
                outJob = false;
            }
        }
    }

    private void terminate() {
        run = false;
    }
}
