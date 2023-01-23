package client.display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Repackaged {

    private final static Gson gson = buildGson();

    private static Gson buildGson() {
        final var gsonBuilder = new GsonBuilder();
        return gsonBuilder
                .disableHtmlEscaping()
                .create();
    }

    @Expose
    private final String response;

    @Expose
    private final String value;

    @Expose
    private final String reason;

    public Repackaged(String response, String value, String reason) {
        this.response = response;
        this.value = value;
        this.reason = reason;
    }

    public String getJson() {
        return gson.toJson(this);
    }
}
