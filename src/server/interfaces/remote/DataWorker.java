package server.interfaces.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.Executor;
import server.interfaces.common.Action;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static common.Message.MESSAGE_TYPE_FIELD;

/**
 * Async.
 * Processes data by reading from request queue and calling the command executor.
 */
public class DataWorker implements Runnable {
    private static final Logger log = Logger.getLogger(DataWorker.class.getSimpleName());

    private static final Gson gson = new GsonBuilder().create();

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
            // take request and process it
            exchange.takeRequest().ifPresent(
                request -> {
                    log.fine("Working on request: " + request);
                    executor.acceptCommand(getCommandFromRequest(request.sessionId(), request.payload()));
                    executor.run();
                });
        }
        log.fine("Stopped.");
    }

    private Command getCommandFromRequest(String sessionId, JsonObject payload) {
        final var type = payload.getAsJsonPrimitive(MESSAGE_TYPE_FIELD);
        if (type == null) {
            throw new RuntimeException("Invalid message, no type was specified: " + payload);
        }
        return remoteCommandFactory.getRemoteCommandFromRequest(
                sessionId,
                Action.from(type.getAsString()),
                payload);
    }
}
