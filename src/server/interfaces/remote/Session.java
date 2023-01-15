package server.interfaces.remote;

import server.interfaces.Exchange;
import server.interfaces.remote.data.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Async.
 * Communicates with the client.
 * In a loop - checks for data to send to client - from the respective response queue.
 * 1. check for requests, enqueue them to request queue
 * 2. check for responses, send them to client
 * We'll be stupid a bit - handle both directions in one session/channel?
 */
//TODO: of course, normally we would protect against backpressure, but normally, we'd not use
// "raw" approach to implement messaging
class Session implements Runnable {
    private static final Logger log = Logger.getLogger(Session.class.getSimpleName());

    private static final int SOCKET_IO_TIMEOUT_MS = 5000;

    private final String sessionId = UUID.randomUUID().toString();

    private final AtomicBoolean stop;
    private final Socket socket;
    private final Exchange exchange;

    Session(AtomicBoolean stop, Socket socket, Exchange exchange) {
        this.stop = stop;
        this.socket = socket;
        this.exchange = exchange;
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
        ) {
            log.fine("[" + sessionId + "]: Started.");
            do {
                try {
                    // process response, if any
                    exchange.takeResponse(sessionId).ifPresent(response -> {
                        try {
                            log.fine("[" + sessionId + "]: Response to send: " + response.payload());
                            outputStream.writeUTF(response.payload());
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to send response to client.");
                        }
                    });
                    // process request
                    if (inputStream.available() > 0) {
                        final var input = inputStream.readUTF();
                        log.fine("[" + sessionId + "]: Input: " + input);
                        final var request = new Request(sessionId, input);
                        exchange.pushRequest(request);
                    }
                } catch (SocketTimeoutException e) {
                    log.finest("[" + sessionId + "]: Socket timeout: just evaluate stop and continue loop");
                }
            } while (exchange.hasPendingResponses(sessionId) || (!stop.get() && !socket.isClosed()));
            log.fine("[" + sessionId + "]: Stopped.");
        } catch (IOException e) {
            log.warning("[" + sessionId + "]: Terminated due to " + e);
        } finally {
            exchange.cleanUp(sessionId);
        }
    }
}
