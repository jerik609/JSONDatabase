package common;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Message {
    public static final String MESSAGE_TYPE_FIELD = "type";
    public static final String MESSAGE_KEY_FIELD = "key";
    public static final String MESSAGE_VALUE_FIELD = "value";

    private static final Gson gson = gsonProvider();

    private static Gson gsonProvider() {
        final var gsonBuilder = new GsonBuilder();
        return gsonBuilder
                //.setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    @Expose
    private final String payload;

    public Message(String payload) {
        this.payload = payload;
    }

    public Message(Response response) {
        this.payload = gson.toJson(response.payload());
    }

    public static Message fromJson(String jsonStr) {
        return gson.fromJson(jsonStr, Message.class);
    }

    public static String[] getKeyAsArrays(JsonObject payload) {
        // key is missing
        final var key = payload.get(MESSAGE_KEY_FIELD);
        if (key == null) {
            throw new RuntimeException("Payload does not contain a key: " + payload);
        }
        // just one element
        if (key instanceof JsonPrimitive primitiveKey) {
            return new String[]{primitiveKey.getAsString()};
        // multiple elements
        } else if (key instanceof JsonArray compoundKey) {
            final var iterator = compoundKey.iterator();
            final ArrayList<String> array = new ArrayList<>();
            while (iterator.hasNext()) {
                final var element = iterator.next();
                // only primitive types allowed
                if (element instanceof JsonPrimitive) {
                    array.add(element.getAsString());
                } else {
                    throw new RuntimeException("Invalid key type " + key);
                }
            }
            return array.toArray(new String[0]);
        } else {
            throw new RuntimeException("Invalid key type " + key);
        }
    }

    public JsonElement getResponse() {
        return gson.fromJson(payload, JsonElement.class);
    }

    public String getWireFormat() {
        return gson.toJson(this);
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "payload='" + payload + '\'' +
                '}';
    }
}
