package server.interfaces.remote;

import server.interfaces.Exchange;
import server.interfaces.Executor;
import server.interfaces.common.Action;
import server.interfaces.common.Utils;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
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
    private final RemoteCommandFactory remoteCommandFactory;
    private final Executor executor;

    public DataWorker(AtomicBoolean stop, ForkJoinPool pool, Exchange exchange, RemoteCommandFactory remoteCommandFactory, Executor executor) {
        this.stop = stop;
        this.pool = pool;
        this.exchange = exchange;
        this.remoteCommandFactory = remoteCommandFactory;
        this.executor = executor;
    }

    public ForkJoinTask<?> start() {
        if (!stop.get() && !isRunning.get()) {
            log.fine("Starting.");
//            var thread = new Thread(this);
//            thread.start();
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
        isRunning.getAndSet(true);
        while (!stop.get() && isRunning.get()) {
            exchange.takeRequest().ifPresent(
                request -> {
                    log.fine("Working on request: " + request);
                    executor.acceptCommand(remoteCommandFactory.getRemoteCommandFromRequest(
                            request.sessionId(),
                            Action.from(request.payload().getRequestType()),
                            request.payload().getCommand()));
                    executor.run();
                });
        }
        log.fine("Stopped.");
    }
}
