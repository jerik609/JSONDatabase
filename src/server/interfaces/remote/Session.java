package server.interfaces.remote;

import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.logging.Logger;

public class Session {
    private static final Logger log = Logger.getLogger(Session.class.getSimpleName());

    private static final int SOCKET_IO_TIMEOUT_MS = 250;

    private final String sessionId = UUID.randomUUID().toString();
    private final Socket socket;

    Session(Socket socket) {
        this.socket = socket;
        try {
            socket.setSoTimeout(SOCKET_IO_TIMEOUT_MS);
        } catch (SocketException e) {
            log.warning("Failed to set the socket timeout: " + e);
            e.printStackTrace();
        }
        log.fine("[" + sessionId + "]: Created.");
    }

    public Socket getSocket() {
        return socket;
    }

    public String getSessionId() {
        return sessionId;
    }
}
