package common.request;

import com.google.gson.annotations.Expose;

public class ExitRemoteRequest implements RemoteRequest {
    @Expose
    private static final String type = "exit";

    @Override
    public String getRequestType() {
        return type;
    }

    @Override
    public String[] getCommand() {
        return new String[0];
    }
}
