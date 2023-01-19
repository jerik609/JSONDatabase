package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import common.request.*;
import common.response.DataRemoteResponse;
import common.response.ErrorRemoteResponse;
import common.response.OkRemoteResponse;
import common.response.RemoteResponse;

public class Message {

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

    public Message(String requestType, String key, String value) {
        final var request = buildRequest(requestType, key, value);
        this.type = request.getRequestType();
        this.payload = gson.toJson(request);
    }

    public Message(RemoteResponse response) {
        this.type = response.getResponseType();
        this.payload = gson.toJson(response);
    }

    public static Message jsonToMessage(String jsonStr) {
        return gson.fromJson(jsonStr, Message.class);
    }

    private static RemoteRequest buildRequest(String requestType, String key, String value) {
        return switch (requestType) {
            case GetRemoteRequest.type -> new GetRemoteRequest(key);
            case SetRemoteRequest.type -> new SetRemoteRequest(key, value);
            case DeleteRemoteRequest.type -> new DeleteRemoteRequest(key);
            case ExitRemoteRequest.type -> new ExitRemoteRequest();
            default -> throw new RuntimeException("Unknown request type: " + requestType);
        };
    }

    public RemoteRequest getRequest() {
        return switch (type) {
            case GetRemoteRequest.type -> gson.fromJson(payload, GetRemoteRequest.class);
            case SetRemoteRequest.type -> gson.fromJson(payload, SetRemoteRequest.class);
            case DeleteRemoteRequest.type -> gson.fromJson(payload, DeleteRemoteRequest.class);
            case ExitRemoteRequest.type -> gson.fromJson(payload, ExitRemoteRequest.class);
            default -> throw new RuntimeException("Unknown remote request type: " + type);
        };
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
}
