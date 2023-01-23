package common.response;

import client.display.Repackaged;

public interface RemoteResponse {
    String getResponseType();
    Repackaged repackage();
}
