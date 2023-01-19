package common.request;

import com.google.gson.annotations.Expose;

public class SetRequest implements Request {
    @Expose
    private static final String type = "set";

    @Expose
    private final String key;

    @Expose
    private final String value;

    public SetRequest(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
