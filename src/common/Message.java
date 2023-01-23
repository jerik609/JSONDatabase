package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import common.request.*;
import common.response.DataRemoteResponse;
import common.response.ErrorRemoteResponse;
import common.response.OkRemoteResponse;
import common.response.RemoteResponse;
import server.interfaces.common.Action;
import server.interfaces.remote.commands.RemoteGetCommand;

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

//    private static RemoteRequest buildRequest(String requestType, String key, String value) {
//        return switch (requestType) {
//            case GetRemoteRequest.type -> new GetRemoteRequest(key);
//            case SetRemoteRequest.type -> new SetRemoteRequest(key, value);
//            case DeleteRemoteRequest.type -> new DeleteRemoteRequest(key);
//            case ExitRemoteRequest.type -> new ExitRemoteRequest();
//            default -> throw new RuntimeException("Unknown request type: " + requestType);
//        };
//    }


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
}
