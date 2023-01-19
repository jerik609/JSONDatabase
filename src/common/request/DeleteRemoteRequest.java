package common.request;

import com.google.gson.annotations.Expose;

public class DeleteRemoteRequest implements RemoteRequest {
    @Expose
    private static final String type = "delete";

    @Expose
    private final String key;

    public DeleteRemoteRequest(String key) {
        this.key = key;
    }

    @Override
    public String getRequestType() {
        return type;
    }
}
