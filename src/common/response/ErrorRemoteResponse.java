package common.response;

import com.google.gson.annotations.Expose;

public class ErrorRemoteResponse implements RemoteResponse {
    @Expose
    public static final String type = "error";

    @Override
    public String getResponseType() {
        return type;
    }
}
