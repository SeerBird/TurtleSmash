package game.connection.gson;

import com.google.gson.*;
import game.connection.packets.containers.images.animations.CollisionBurstAnimationImage;
import game.connection.packets.containers.images.animations.ScreenShakeAnimationImage;
import game.connection.packets.containers.images.animations.ShellSnapFlashAnimationImage;
import game.connection.packets.containers.images.bodies.ShellImage;
import game.connection.packets.containers.images.bodies.TurtleImage;
import game.connection.packets.containers.images.bodies.WebImage;
import game.connection.packets.containers.images.edges.BodyEdgePointer;
import game.connection.packets.containers.images.edges.ControlEdgePointer;
import game.connection.packets.containers.images.edges.FixedEdgeImage;
import game.connection.packets.containers.images.edges.WorldEdgeImage;
import game.output.animations.CollisionBurstAnimation;
import game.output.animations.ScreenShakeAnimation;
import game.output.animations.ShellSnapFlashAnimation;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private static final Map<Class<?>, String> classTokens = Map.of(
            CollisionBurstAnimationImage.class, "CB",
            ScreenShakeAnimationImage.class, "SS",
            ShellSnapFlashAnimationImage.class, "SF",
            ShellImage.class, "SI",
            TurtleImage.class, "TI",
            WebImage.class, "WI",
            FixedEdgeImage.class, "FE",
            WorldEdgeImage.class, "WE",
            ControlEdgePointer.class, "CE",
            BodyEdgePointer.class, "BE"
    );

    private static final String CLASSNAME = "C";
    private static final String DATA = "D";

    public T deserialize(JsonElement jsonElement, Type type,
                         JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();
        Class<?> klass = getObjectClass(className);
        return jsonDeserializationContext.deserialize(jsonObject.get(DATA), klass);
    }

    public JsonElement serialize(T jsonElement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CLASSNAME, classTokens.get(jsonElement.getClass()));
        jsonObject.add(DATA, jsonSerializationContext.serialize(jsonElement));
        return jsonObject;
    }

    /****** Helper method to get the className of the object to be deserialized *****/
    public Class<?> getObjectClass(String className) {
        for (Class<?> klass : classTokens.keySet()) {
            if (Objects.equals(classTokens.get(klass), className)) {
                return klass;
            }
        }
        return Object.class;
    }
}
