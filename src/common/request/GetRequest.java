package common.request;

import com.google.gson.annotations.Expose;

public class GetRequest implements Request {
    @Expose
    private static final String type = "get";

    @Expose
    private final String key;

    public GetRequest(String key) {
        this.key = key;
    }
}
