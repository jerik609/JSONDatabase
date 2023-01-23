package common.response;

import client.display.Repackaged;
import com.google.gson.annotations.Expose;

public class OkRemoteResponse implements RemoteResponse {
    @Expose
    public static final String type = "ok";

    @Override
    public String getResponseType() {
        return type;
    }

    @Override
    public Repackaged repackage() {
        return new Repackaged("OK", null, null);
    }
}
