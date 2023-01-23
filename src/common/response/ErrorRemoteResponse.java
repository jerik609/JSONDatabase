package common.response;

import client.display.Repackaged;
import com.google.gson.annotations.Expose;

public class ErrorRemoteResponse implements RemoteResponse {
    @Expose
    public static final String type = "error";
    @Expose
    private final String reason;

    public ErrorRemoteResponse(String reason) {
        this.reason = reason;
    }


    @Override
    public String getResponseType() {
        return type;
    }

    @Override
    public Repackaged repackage() {
        return new Repackaged("ERROR", null, reason);
    }
}
