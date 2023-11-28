package game.util;

import game.MainLoop;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Util {
    public static String path;

    static {
        try {
            String prefix = "/TurtleSmash/";//remove TurtleSmash for windows?
            //region get log path
            String os = System.getProperty("os.name");
            URI uri;
            if (os.contains("Windows")) { //check separate versions?
                path = (System.getenv("LOCALAPPDATA") + prefix).replaceAll("\\\\", "/");
                uri = URI.create("file:/" + path.substring(0, path.length() - 1));
                Files.createDirectories(Paths.get(uri));
            } else if (Objects.equals(os, "MacOS")) { //figure this out
                path = System.getProperty("user.dir");
                uri = URI.create("file:/" + path);
            } else if (os.contains("Linux")) {
                path = System.getProperty("user.dir");
                uri = URI.create("file:///tmp/" + path); //find another path? tmp ain't nice
            } else {
                path = System.getProperty("user.dir");
                uri = URI.create("file:/" + path);
            }
            //endregion
        } catch (Exception death) {
            MainLoop.terminate();
        }
    }
}
