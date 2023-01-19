package common.request;

import com.google.gson.annotations.Expose;

public class SetRemoteRequest implements RemoteRequest {
    @Expose
    public static final String type = "set";

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

    @Override
    public String[] getCommand() {
        return new String[]{key, value};
    }

    @Override
    public String toString() {
        return "SetRemoteRequest{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
