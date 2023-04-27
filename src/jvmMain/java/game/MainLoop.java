package game;

public class MainLoop {
    static boolean run;

    public static void start() {
        run = true;
        int minFrameTime = 1000000000 / CONSTANTS.MAX_FRAMERATE; // nano
        EventManager manager = new EventManager();
        boolean outJob = false;
        long last = 0;
        long now;
        int updateJobs = 0;
        while (run) {// weird, needs a server game.input, is horrible atm but doesn't matter
            now = System.nanoTime();
            long timeLeft = minFrameTime - now + last;
            if (timeLeft <= 0) {
                updateJobs++;
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
