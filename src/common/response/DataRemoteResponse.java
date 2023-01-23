package common.response;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class DataRemoteResponse implements RemoteResponse {
    @Expose
    public static final String type = "ok_data";

    @Expose
    private final String value;

    public DataRemoteResponse(String value) {
        this.value = value;
    }

    @Override
    public String getResponseType() {
        return type;
    }

    @Override
    public JsonObject getAsJsonObject() {
        final var gson = new GsonBuilder().setPrettyPrinting().create();



        System.out.println(value);
        final var jsonObject = gson.fromJson(value, JsonObject.class);
        System.out.println("xxx:" + gson.toJson(jsonObject));
        return jsonObject;
    }

    @Override
    public String toString() {
        return "DataRemoteResponse{" +
                "value='" + value + '\'' +
                '}';
    }
}
