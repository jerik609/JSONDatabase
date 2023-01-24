package common;

import com.google.gson.JsonObject;

public record Response(String sessionId, JsonObject payload) {
}
