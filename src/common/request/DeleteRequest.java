package common.request;

import com.google.gson.annotations.Expose;

public class DeleteRequest implements Request {
    @Expose
    private static final String type = "delete";

    @Expose
    private final String key;

    public DeleteRequest(String key) {
        this.key = key;
    }
}
