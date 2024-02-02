package game.connection.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import game.connection.packets.containers.images.animations.AnimationImage;
import game.connection.packets.containers.images.bodies.BodyImage;
import game.connection.packets.containers.images.edges.*;
import javafx.util.Pair;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class gsonRegistry {
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(EdgeImage.class, new InterfaceAdapter<EdgeImage>())
            .registerTypeAdapter(BodyImage.class, new InterfaceAdapter<BodyImage>())
            .registerTypeAdapter(AnimationImage.class, new InterfaceAdapter<AnimationImage>())
            .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.HALF_UP);
                return new JsonPrimitive(Double.parseDouble(df.format(src)));
            })
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .serializeSpecialFloatingPointValues()
            .create();
}
