package server.input.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private static final int LISTENER_PORT = 34567;
    private static final int SERVER_SOCKET_TIMEOUT_MS = 10000;

    private final ForkJoinPool pool; //TODO: project loom, where are you?
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean stop = new AtomicBoolean(false);

    public Server() {
        pool = new ForkJoinPool(4);
    }

    public void start() {
        if (!isRunning.get()) {
            pool.submit(this);
        }
    }

    @Override
    public void run() {
        try (
                final var serverSocket = new ServerSocket(LISTENER_PORT);
                )
        {
            //System.out.println("Server: Starting listening for client connections.");
            System.out.println("Server started!");
            serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT_MS);
            do {
                try {
                    final var socket = serverSocket.accept();
                    //System.out.println("Server: New client established connection.");
                    pool.submit(new Session(socket, stop));
                } catch (SocketTimeoutException e) {
                    //System.out.println("Server: Server socket timeout, just evaluate stop and continue loop");
                }
            } while (!stop.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (isRunning.get()) {
            //System.out.println("Server: Server not running or already shutting down, nothing to be done.");
        } else {
            //System.out.println("Server: Stopping the server gracefully.");
            // perform shutdown - signal threads and wait for completion
            stop.getAndSet(true);
            pool.shutdown();
            try {
                if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Server: Thread pool failed to stop gracefully");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Server: Interrupted while trying to stop the thread pool");
            }
            // ensure consistent state in case we'd like to start the server again
            stop.getAndSet(false);
            isRunning.getAndSet(false);
            //System.out.println("Server: Server stopped gracefully.");
        }
    }

    private static class Session implements Runnable {
        private static final int SOCKET_IO_TIMEOUT_MS = 5000;
        private final String sessionId = UUID.randomUUID().toString();
        private final Socket socket;
        private final AtomicBoolean stop;

        private Session(Socket socket, AtomicBoolean stop) {
            this.socket = socket;
            this.stop = stop;
            //System.out.println("[" + sessionId + "]: Created.");
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
                    )
            {
                //System.out.println("[" + sessionId + "]: Starting.");
                String input;
                do {
                    try {
                        input = inputStream.readUTF();
                        //System.out.println("[" + sessionId + "]: Client says: " + input);
                        System.out.println("Received: " + input);
                        System.out.println("Sent: " + input);
                        outputStream.writeUTF(input);
                    } catch (SocketTimeoutException e) {
                        //System.out.println("[" + sessionId + "]: Socket timeout: just evaluate stop and continue loop");
                    }
                } while (!stop.get() && socket.isConnected());
                //System.out.println("[" + sessionId + "]: Done, terminating.");
            } catch (IOException e) {
                throw new RuntimeException("[" + sessionId + "]: failed.");
            }
        }
    }
}
