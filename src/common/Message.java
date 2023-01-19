package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import common.request.DeleteRemoteRequest;
import common.request.GetRemoteRequest;
import common.request.RemoteRequest;
import common.request.SetRemoteRequest;

public class Message {

    private static Gson gson = gsonProvider();

    private static Gson gsonProvider() {
        final var gsonBuilder = new GsonBuilder();
        return gsonBuilder
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    @Expose
    private final String type;
    @Expose
    private final String payload;

    public Message(RemoteRequest request) {
        this.type = request.getRequestType();
        this.payload = gson.toJson(request);
    }

    public static Message jsonToMessage(String jsonStr) {
        return gson.fromJson(jsonStr, Message.class);
    }

    public RemoteRequest getRequest() {
        return switch (type) {
            case "get" -> gson.fromJson(payload, GetRemoteRequest.class);
            case "set" -> gson.fromJson(payload, SetRemoteRequest.class);
            case "delete" -> gson.fromJson(payload, DeleteRemoteRequest.class);
            default -> throw new RuntimeException("Unknown remote request type: " + type);
        };
    }

    public String getWireFormat() {
        return gson.toJson(this);
    }
}
