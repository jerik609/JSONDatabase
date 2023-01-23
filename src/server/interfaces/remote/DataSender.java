package server.interfaces.remote;

import common.Message;
import server.interfaces.Exchange;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Async.
 * Communicates with the client.
 * Check for responses, send them to client.
 */
public class DataSender implements Runnable {
    private static final Logger log = Logger.getLogger(DataSender.class.getSimpleName());

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final AtomicBoolean stop;
    private final ForkJoinPool pool;
    private final Exchange exchange;

    public DataSender(AtomicBoolean stop, ForkJoinPool pool, Exchange exchange) {
        this.stop = stop;
        this.pool = pool;
        this.exchange = exchange;
    }

    public ForkJoinTask<?> start() {
        if (!stop.get() && !isRunning.get()) {
            log.fine("Starting.");
            return pool.submit(this);
        } else {
            log.fine("Cannot start.");
            return null;
        }
    }

    public void stop() {
        if (isRunning.get()) {
            log.fine("Stopping.");
            isRunning.getAndSet(false);
        } else {
            log.fine("Cannot stop.");
        }
    }

    @Override
    public void run() {
        log.fine("Started.");
        while (!stop.get() || exchange.hasPendingResponses()) {
            for (final var session : exchange.getSessions()) {
                final var outputStream = session.getOutputStream();
                // process response, if any
                exchange.takeResponse(session.getSessionId()).ifPresent(response -> {
                    try {
                        log.fine("[" + session.getSessionId() + "]: Response to send: " + response.payload());
                        final var msg = new Message(response);
                        final var wireFormat = msg.getWireFormat();
                        log.fine("wire format: " + wireFormat);
                        outputStream.writeUTF(wireFormat);
                    } catch (IOException e) {
                        log.warning("Failed to send response to client: " + e);
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        log.fine("Stopped.");
    }
}
