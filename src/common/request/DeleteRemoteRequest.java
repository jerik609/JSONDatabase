package common.request;

import com.google.gson.annotations.Expose;

public class DeleteRemoteRequest implements RemoteRequest {
    @Expose
    public static final String type = "delete";

    @Expose
    private final String key;

    public DeleteRemoteRequest(String key) {
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
        return "DeleteRemoteRequest{" +
                "key='" + key + '\'' +
                '}';
    }
}
