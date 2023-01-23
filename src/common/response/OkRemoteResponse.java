package common.response;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class OkRemoteResponse implements RemoteResponse {
    @Expose
    public static final String type = "ok";

    @Override
    public String getResponseType() {
        return type;
    }

    @Override
    public JsonObject getAsJsonObject() {
        return null;
    }
}
