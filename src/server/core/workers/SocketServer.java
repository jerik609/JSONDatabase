package server.core.workers;

import server.core.data.Exchange;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
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
    private static final int SERVER_SOCKET_TIMEOUT_MS = 10000;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final AtomicBoolean stop;
    private final ForkJoinPool pool;
    private final Exchange exchange;

    public SocketServer(AtomicBoolean stop, ForkJoinPool pool, Exchange exchange) {
        this.stop = stop;
        this.pool = pool;
        this.exchange = exchange;
    }

    public void start() {
        if (!stop.get() && !isRunning.get()) {
            log.fine("Starting.");
            pool.submit(this);
            log.fine("Started.");
        } else {
            log.fine("Cannot start.");
        }
    }

    @Override
    public void run() {
        try (
                final var serverSocket = new ServerSocket(LISTENER_PORT);
        )
        {
            log.fine("[Server]: Starting listening for client connections.");
            System.out.println("Server started!");
            serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT_MS);
            do {
                try {
                    final var socket = serverSocket.accept();
                    log.fine("[Server]: New client established connection.");
                    pool.submit(new Session(stop, socket, exchange));
                } catch (SocketTimeoutException e) {
                    log.finest("[Server]: Server socket timeout, just evaluate stop and continue loop");
                }
            } while (!stop.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.fine("[Server]: Finished processing.");
    }

    //TODO: this will not work!
    @Deprecated
    public void stop() {
        if (isRunning.get()) {
            log.fine("[Server]: Server not running or already shutting down, nothing to be done.");
        } else {
            log.fine("[Server]: Stopping the server gracefully.");
            // perform shutdown - signal threads and wait for completion
            stop.getAndSet(true);
            pool.shutdown();
            try {
                if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Thread pool failed to stop gracefully");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while trying to stop the thread pool");
            }
            // ensure consistent state in case we'd like to start the server again
            stop.getAndSet(false);
            isRunning.getAndSet(false);
            log.fine("[Server]: Server stopped gracefully.");
        }
    }
}
