package common.response;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class ErrorRemoteResponse implements RemoteResponse {
    @Expose
    public static final String type = "error";
    @Expose
    private final String reason;

    public ErrorRemoteResponse(String reason) {
        this.reason = reason;
    }


    @Override
    public String getResponseType() {
        return type;
    }

    @Override
    public JsonObject getAsJsonObject() {
        return null;
    }
}
