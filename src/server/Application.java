package server;

import server.interfaces.Exchange;
import server.interfaces.local.Console;
import server.interfaces.local.LocalCommandFactory;
import server.interfaces.remote.*;
import server.database.Database;
import server.interfaces.Executor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.*;

import static java.lang.Thread.sleep;

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

        System.out.println("hmm?");

        final var stopFlag = new AtomicBoolean(false);

        System.out.println("hmm?");

        //final var scanner = new Scanner(System.in);
        final var executor = new Executor();
        final var database = new Database<>(1000, "");

        System.out.println("hmm?");

        // local context
        //final var localCommandFactory = new LocalCommandFactory(stopFlag, database);
        //final var console = new Console(stopFlag, scanner, localCommandFactory, executor);

        System.out.println("hmm?");

        // remote context
        final var pool = new ForkJoinPool(6);

        System.out.println("hmm?");

        final var exchange = new Exchange();
        final var remoteCommandFactory = new RemoteCommandFactory(stopFlag, database, exchange);

        System.out.println("hmm?");

        final var dataWorker = new DataWorker(stopFlag, pool, exchange, remoteCommandFactory, executor);
        final var dataReader = new DataReader(stopFlag, pool, exchange);
        final var dataSender = new DataSender(stopFlag, pool, exchange);
        final var socketServer = new SocketServer(stopFlag, pool, exchange);

        System.out.println("Server started!");

        // start the application
        dataSender.start();

        System.out.println("hmm?!! 1");
        dataWorker.start();
        System.out.println("hmm?!!! 2");
        dataReader.start();
        System.out.println("hmm?!!! 3");
        socketServer.start();

        System.out.println("hmm?!!! 4");

        System.out.println("start" + LocalTime.now());

        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("slept" + LocalTime.now());

        //console.start();

        //TODO: remove after tests are done?
//        while (!stopFlag.get()) {
//            System.out.println("ffffffffffff");
//            try {
//                sleep(500);
//            } catch (InterruptedException e) {
//                System.out.println("uuuuuuuuuuuu");
//                throw new RuntimeException(e);
//            }
//            System.out.println("zzzzzzzzzzzzzz");
//        }
        //System.exit(0);

        // sync thread pool shutdown
//        pool.shutdown();
//        try {
//            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
//                throw new RuntimeException("Thread pool failed to stop gracefully");
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException("Interrupted while trying to stop the thread pool");
//        }

        System.out.println("Terminated");

        log.fine("Application terminated gracefully.");
    }
}
