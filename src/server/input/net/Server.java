package server.input.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

// server is the owner of other components ... well, server has a listener part, but definitely
// this code should not be at the bottom at the hierarchy

// server

// several threads
// main communication loop - listens async for new connections and creates sessions
// sessions are spawned as separate threads and given a command queue with session id
// backpressure? well, ringbuffer would be good, but we're not here to implement a new
// messaging solution, are we ;-)

// there's a request sync and a response source

// but let's not get ahead of ourselves :-)

// TODO: the idea will not work -> we need something that interrupts the wait and then checks for stop
//  maybe the fork join pool does that on its own?
public class Server implements Runnable {
    private static final Logger log = Logger.getLogger(Server.class.getSimpleName());

    private static final int LISTENER_PORT = 34567;
    private static final int SERVER_SOCKET_TIMEOUT_MS = 10000;

    private final ForkJoinPool pool; //TODO: project loom, where are you?
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean stop = new AtomicBoolean(false);


    public Server() {
        pool = new ForkJoinPool(4);
    }

    public void start() {
        log.fine("[Server]: Starting.");
        if (!isRunning.get()) {
            pool.submit(this);
        }
        log.fine("[Server]: Started.");
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
                    pool.submit(new Session(socket, stop));
                } catch (SocketTimeoutException e) {
                    log.finest("[Server]: Server socket timeout, just evaluate stop and continue loop");
                }
            } while (!stop.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.fine("[Server]: Finished processing.");
    }

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
