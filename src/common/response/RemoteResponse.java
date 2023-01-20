package common.response;

import client.Repackaged;

public interface RemoteResponse {
    String getResponseType();
    Repackaged repackage();
}
