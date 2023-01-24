package common;

import com.google.gson.JsonObject;

public record Request(String sessionId, JsonObject payload) {
}
