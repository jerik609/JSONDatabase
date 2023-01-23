package common;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import common.response.DataRemoteResponse;
import common.response.ErrorRemoteResponse;
import common.response.OkRemoteResponse;
import common.response.RemoteResponse;

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
    private final String type;
    @Expose
    private final String payload;

    public Message(String payload) {
        this.type = "";
        this.payload = payload;
    }

//    public Message(String requestType, String key, String value) {
//        final var request = buildRequest(requestType, key, value);
//        this.type = request.getRequestType();
//        this.payload = gson.toJson(request);
//    }

    public Message(RemoteResponse response) {
        this.type = response.getResponseType();
        this.payload = gson.toJson(response);
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

    public RemoteResponse getResponse() {
        return switch (type) {
            case OkRemoteResponse.type -> gson.fromJson(payload, OkRemoteResponse.class);
            case DataRemoteResponse.type -> gson.fromJson(payload, DataRemoteResponse.class);
            case ErrorRemoteResponse.type -> gson.fromJson(payload, ErrorRemoteResponse.class);
            default -> throw new RuntimeException("Unknown remote response type: " + type);
        };
    }

    public String getWireFormat() {
        return gson.toJson(this);
    }

    public String getPayload() {
        return payload;
    }

    public String getFooPrint() {
        return "{\"type\":\"" + type + "\"," + payload.substring(1);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
