package game.connection.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.connection.packets.containers.images.bodies.BodyImage;
import game.connection.packets.containers.images.edges.*;
import javafx.util.Pair;

public class gsonRegistry {
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(EdgeImage.class, new InterfaceAdapter<EdgeImage>())
            .registerTypeAdapter(BodyImage.class, new InterfaceAdapter<BodyImage>())
            //.registerTypeAdapter(Pair.class, new InterfaceAdapter<Pair>())
            .serializeSpecialFloatingPointValues()
            .create();
}
