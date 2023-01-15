package server.interfaces.remote;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.logging.Logger;

public class Session {
    private static final Logger log = Logger.getLogger(Session.class.getSimpleName());

    private static final int SOCKET_IO_TIMEOUT_MS = 250;

    private final String sessionId = UUID.randomUUID().toString();
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    Session(Socket socket) {
        try {
            socket.setSoTimeout(SOCKET_IO_TIMEOUT_MS);
        } catch (SocketException e) {
            log.warning("Failed to set the socket timeout: " + e);
            e.printStackTrace();
        }
        try {
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            log.warning("Failed to setup session: " + e);
            throw new RuntimeException(e);
        }
        log.fine("[" + sessionId + "]: Created.");
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public String getSessionId() {
        return sessionId;
    }
}
