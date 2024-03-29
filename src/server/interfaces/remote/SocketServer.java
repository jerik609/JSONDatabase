package server.interfaces.remote;

import server.interfaces.Exchange;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Async.
 * Listens for new connections.
 * Creates session objects (we can use a factory later, but no reason for it now).
 * Gives the session objects two queues -> request queue and response queue.
 */
public class SocketServer implements Runnable {
    private static final Logger log = Logger.getLogger(SocketServer.class.getSimpleName());

    private static final int LISTENER_PORT = 34567;
    private static final int SERVER_SOCKET_TIMEOUT_MS = 50;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final AtomicBoolean stop;
    private final ForkJoinPool pool;
    private final Exchange exchange;

    public SocketServer(AtomicBoolean stop, ForkJoinPool pool, Exchange exchange) {
        this.stop = stop;
        this.pool = pool;
        this.exchange = exchange;
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

        try (
                final var serverSocket = new ServerSocket(LISTENER_PORT);
        ) {
            serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT_MS);
            while (!stop.get() && isRunning.get()) {
                try {
                    final var socket = serverSocket.accept();
                    log.fine("New client established connection.");

                    exchange.addSession(new Session(socket));

                    //pool.submit(new Session(stop, socket, exchange));
                } catch (SocketTimeoutException e) {
                    //log.finest("Server socket timeout, just evaluate stop and continue loop");
                }
            }
        } catch (IOException e) {
            log.severe("Unexpected exception when executing command: " + e);
            e.printStackTrace();
        }

        log.fine("Stopped.");
    }
}
