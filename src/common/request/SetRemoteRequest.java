package common.request;

import com.google.gson.annotations.Expose;

public class SetRemoteRequest implements RemoteRequest {
    @Expose
    private static final String type = "set";

    @Expose
    private final String key;

    @Expose
    private final String value;

    public SetRemoteRequest(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getRequestType() {
        return type;
    }
}
