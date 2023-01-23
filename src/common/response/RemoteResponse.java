package common.response;

import com.google.gson.JsonObject;

public interface RemoteResponse {
    String getResponseType();
    JsonObject getAsJsonObject();
}
