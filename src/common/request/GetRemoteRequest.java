package common.request;

import com.google.gson.annotations.Expose;

public class GetRemoteRequest implements RemoteRequest {
    @Expose
    private static final String type = "get";

    @Expose
    private final String key;

    public GetRemoteRequest(String key) {
        this.key = key;
    }

    @Override
    public String getRequestType() {
        return type;
    }

    @Override
    public String[] getCommand() {
        return new String[]{key};
    }

    @Override
    public String toString() {
        return "GetRemoteRequest{" +
                "key='" + key + '\'' +
                '}';
    }
}
