package server;

import server.database.Persistence;
import server.interfaces.Exchange;
import server.interfaces.local.Console;
import server.interfaces.local.LocalCommandFactory;
import server.interfaces.remote.*;
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

    private static final String DATE_TIME_PATTERN_FORMAT = "dd.MM.yyyy-HH:mm:ss.SSS";

    // set logging, due to structure of the academy project, we do it in the code and not via configuration file
    public static void setLogging(Level logLevel) {
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
        rootLogger.setLevel(logLevel);
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.FINEST);
            handler.setFormatter(formatter);
            log.fine("Setting log level and formatter for handler: " + handler);
        }
    }

    public void start(Level logLevel) {
        setLogging(logLevel);

        log.fine("---=== Starting JSON Database Application ===---");

        final var stopFlag = new AtomicBoolean(false);

        final var scanner = new Scanner(System.in);
        final var executor = new Executor();
        final var database = Persistence.loadDbFromFile().orElse(new Database(1000));

        // local context
        final var localCommandFactory = new LocalCommandFactory(stopFlag, database);
        final var console = new Console(stopFlag, scanner, localCommandFactory, executor);

        // remote context
        final var pool = new ForkJoinPool(6);

        final var exchange = new Exchange();
        final var remoteCommandFactory = new RemoteCommandFactory(stopFlag, database, exchange);

        final var dataWorker = new DataWorker(stopFlag, pool, exchange, remoteCommandFactory, executor);
        final var dataReader = new DataReader(stopFlag, pool, exchange);
        final var dataSender = new DataSender(stopFlag, pool, exchange);
        final var socketServer = new SocketServer(stopFlag, pool, exchange);

        System.out.println("Server started!");

        // start the application
        final var dataSenderTask = dataSender.start();
        final var dataWorkerTask = dataWorker.start();
        final var dataReaderTask = dataReader.start();
        final var socketServerTask = socketServer.start();

        //console.start();

        // wait for processors to finish
        socketServerTask.join();
        log.fine("SocketServer finished.");
        dataReaderTask.join();
        log.fine("DataReader finished.");
        dataWorkerTask.join();
        log.fine("DataWorker finished.");
        dataSenderTask.join();
        log.fine("DataSender finished.");

        Persistence.persistDatabase(database);

        // sync thread pool shutdown
        pool.shutdown();
        try {
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Thread pool failed to stop gracefully");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to stop the thread pool");
        }

        log.fine("Application terminated gracefully.");
    }
}
