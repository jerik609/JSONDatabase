package server.interfaces.remote.data;

import common.Message;

public record Request(String sessionId, Message message) {
}
