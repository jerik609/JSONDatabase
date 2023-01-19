package common.response;

import com.google.gson.annotations.Expose;

public class DataRemoteResponse implements RemoteResponse {
    @Expose
    public static final String type = "ok_data";

    @Expose
    private final String value;

    public DataRemoteResponse(String value) {
        this.value = value;
    }

    @Override
    public String getResponseType() {
        return type;
    }

    @Override
    public String toString() {
        return "DataRemoteResponse{" +
                "value='" + value + '\'' +
                '}';
    }
}
