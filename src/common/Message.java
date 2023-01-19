package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import common.request.*;

public class Message {

    private static final Gson gson = gsonProvider();

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

    public Message(String requestType, String key, String value) {
        final var request = buildRequest(requestType, key, value);
        this.type = request.getRequestType();
        this.payload = gson.toJson(request);
    }

    public static Message jsonToMessage(String jsonStr) {
        return gson.fromJson(jsonStr, Message.class);
    }

    private static RemoteRequest buildRequest(String requestType, String key, String value) {
        return switch (requestType) {
            case "get" -> new GetRemoteRequest(key);
            case "set" -> new SetRemoteRequest(key, value);
            case "delete" -> new DeleteRemoteRequest(key);
            case "exit" -> new ExitRemoteRequest();
            default -> throw new RuntimeException("Invalid request: " + requestType);
        };
    }

    public RemoteRequest getRequest() {
        return switch (type) {
            case "get" -> gson.fromJson(payload, GetRemoteRequest.class);
            case "set" -> gson.fromJson(payload, SetRemoteRequest.class);
            case "delete" -> gson.fromJson(payload, DeleteRemoteRequest.class);
            case "exit" -> gson.fromJson(payload, ExitRemoteRequest.class);
            default -> throw new RuntimeException("Unknown remote request type: " + type);
        };
    }

    public String getWireFormat() {
        return gson.toJson(this);
    }
}
