package server.core;

import server.interfaces.Exchange;
import server.interfaces.remote.workers.DataWorker;
import server.interfaces.remote.workers.SocketServer;
import server.database.Database;
import server.interfaces.Executor;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.*;

public class Application {
    private static final Logger log = Logger.getLogger(Application.class.getSimpleName());

    private static final String DATE_TIME_PATTERN_FORMAT = "dd.MM.yyyy-HH:mm:ss";

    // set logging, due to structure of the academy project, we do it in the code and not via configuration file
    public static void setLogging() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_FORMAT)
                .withZone(ZoneId.systemDefault());
        var formatter = new Formatter() {
            @Override
            public String format(LogRecord logRecord) {
                return "{" + timeFormatter.format(logRecord.getInstant()) + "} - "
                        + "[" + logRecord.getLevel() + "] - "
                        + "(" + logRecord.getLoggerName() + "," + logRecord.getLongThreadID() + ") - "
                        + logRecord.getMessage() + "\n";
            }
        };
        var rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.FINEST);
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.FINEST);
            handler.setFormatter(formatter);
            log.fine("Setting log level and formatter for handler: " + handler);
        }
    }

    public void start() {
        setLogging();

        final var scanner = new Scanner(System.in);
        final var executor = new Executor();
        final var database = new Database<>(1000, "");

        final var stopFlag = new AtomicBoolean(false);

        final var controller = new Controller(scanner, executor, database);

        // messaging layer

        final var pool = new ForkJoinPool(4);
        final var exchange = new Exchange();

        final var dataWorker = new DataWorker(stopFlag, pool, exchange);
        final var socketServer = new SocketServer(stopFlag, pool, exchange);

        dataWorker.start();
        socketServer.start();

        controller.start();

//        socketServer.stop();
//        dataWorker.stop();

        //TODO: this shutdown is a bit dirty, we'll clean up later
        stopFlag.getAndSet(true);

        pool.shutdown();
        try {
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Thread pool failed to stop gracefully");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to stop the thread pool");
        }
    }
}
