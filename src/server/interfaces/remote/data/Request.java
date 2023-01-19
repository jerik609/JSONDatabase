package server.interfaces.remote.data;

import common.request.RemoteRequest;

public record Request(String sessionId, RemoteRequest payload) {
}
