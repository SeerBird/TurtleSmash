package game.connection.gson;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
    final static String red = "R";
    final static String green = "G";
    final static String blue = "B";
    final static String alpha = "A";

    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        return new Color(obj.get(red).getAsInt(), obj.get(green).getAsInt(), obj.get(blue).getAsInt(), obj.get(alpha).getAsInt());
    }

    @Override
    public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(red, context.serialize(src.getRed()));
        jsonObject.add(green, context.serialize(src.getGreen()));
        jsonObject.add(blue, context.serialize(src.getBlue()));
        jsonObject.add(alpha, context.serialize(src.getAlpha()));
        return jsonObject;
    }
}
