package server.interfaces.remote;

import common.Message;
import server.interfaces.Exchange;
import common.Request;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Async.
 * Communicates with the client.
 * Check for requests, enqueue them to request queue.
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
        while (!stop.get()) {
            for (final var session : exchange.getSessions()) {
                final var inputStream = session.getInputStream();
                try {
                    if (inputStream.available() > 0) {
                        final var message = Message.fromJson(inputStream.readUTF());
                        log.fine("[" + session.getSessionId() + "]: received message:\n" + message);
                        exchange.pushRequest(new Request(session.getSessionId(), message.getPayload()));
                    }
                } catch (IOException e) {
                    log.warning("Failed to read from input stream: " + e);
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        log.fine("Stopped.");
    }
}
