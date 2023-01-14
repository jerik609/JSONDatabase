package server.input.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static server.input.common.Utils.getRecordNoFromResponse;

class Session implements Runnable {
    private static final Logger log = Logger.getLogger(Session.class.getSimpleName());

    private static final int SOCKET_IO_TIMEOUT_MS = 5000;
    private final String sessionId = UUID.randomUUID().toString();
    private final Socket socket;
    private final AtomicBoolean stop;

    Session(Socket socket, AtomicBoolean stop) {
        this.socket = socket;
        this.stop = stop;
        log.fine("[" + sessionId + "]: Created.");
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(SOCKET_IO_TIMEOUT_MS);
        } catch (SocketException e) {
            throw new RuntimeException("Failed to set socket timeout");
        }
        try (
                socket;
                final var inputStream = new DataInputStream(socket.getInputStream());
                final var outputStream = new DataOutputStream(socket.getOutputStream());
        )
        {
            log.fine("[" + sessionId + "]: Starting.");
            String input;
            do {
                try {
                    input = inputStream.readUTF();
                    log.fine("[" + sessionId + "]: Client sent: " + input);
                    System.out.println("Received: " + input);
                    final var recordNo = getRecordNoFromResponse(input);
                    final var response = "A record # " + recordNo + " was sent!";
                    System.out.println("Sent: " + response);
                    outputStream.writeUTF(response);
                } catch (SocketTimeoutException e) {
                    log.fine("[" + sessionId + "]: Socket timeout: just evaluate stop and continue loop");
                }
            } while (!stop.get() && socket.isConnected());
            log.fine("[" + sessionId + "]: Done, terminating.");
        } catch (IOException e) {
            throw new RuntimeException("[" + sessionId + "]: Failed.");
        }
    }
}