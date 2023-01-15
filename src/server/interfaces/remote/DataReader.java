package server.interfaces.remote;

import server.interfaces.Exchange;
import server.interfaces.remote.data.Request;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
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
public class DataReader implements Runnable {
    private static final Logger log = Logger.getLogger(DataReader.class.getSimpleName());

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final AtomicBoolean stop;
    private final ForkJoinPool pool;
    private final Exchange exchange;

    public DataReader(AtomicBoolean stop, ForkJoinPool pool, Exchange exchange) {
        this.stop = stop;
        this.pool = pool;
        this.exchange = exchange;
    }

    public void start() {
        if (!stop.get() && !isRunning.get()) {
            log.fine("Starting.");
            pool.submit(this);
        } else {
            log.fine("Cannot start.");
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
        while (!stop.get()) {
            for (final var session : exchange.getSessions()) {
                try (
                        final var inputStream = new DataInputStream(session.getSocket().getInputStream())
                ) {
                    if (inputStream.available() > 0) {
                        final var input = inputStream.readUTF();
                        log.fine("[" + session.getSessionId() + "]: Input: " + input);
                        final var request = new Request(session.getSessionId(), input);
                        exchange.pushRequest(request);
                    }
                } catch (IOException e) {
                    log.warning("Unexpected exception while reading data from session: " + e);
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        log.fine("Stopped.");
    }
}
