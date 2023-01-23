package common.response;

import com.google.gson.JsonElement;

public record Response(String sessionId, JsonElement payload) {
}
