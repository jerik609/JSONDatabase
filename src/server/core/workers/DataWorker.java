package server.core.workers;

import server.core.data.Exchange;
import server.core.data.Response;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Async.
 * Processes data by reading from request queue and calling the command executor.
 */
public class DataWorker implements Runnable {
    private static final Logger log = Logger.getLogger(DataWorker.class.getSimpleName());

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final AtomicBoolean stop;
    private final ForkJoinPool pool;
    private final Exchange exchange;

    public DataWorker(AtomicBoolean stop, ForkJoinPool pool, Exchange exchange) {
        this.exchange = exchange;
        this.pool = pool;
        this.stop = stop;
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
        isRunning.getAndSet(true);
        while (!stop.get() && isRunning.get()) {
            exchange.takeRequest().ifPresent(
                    //TODO: call the command processor here:
                    // provide the response queue or just the exchange?
                    // command processor will generate the response and put it in the queue
                    request -> exchange.pushResponse(new Response(request.sessionId(), "ECHO: " + request.payload()))
            );
        }
        log.fine("Stopped.");
    }
}
