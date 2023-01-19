package server.interfaces.remote.data;

import common.response.RemoteResponse;

public record Response(String sessionId, RemoteResponse payload) {
}
