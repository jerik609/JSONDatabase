package server;

import server.database.Database;
import server.input.Controller;
import server.input.Executor;
import server.input.net.Server;

import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Application {
    private static final Logger log = Logger.getLogger(Application.class.getSimpleName());
    public void start() {
        // set logging, due to structure of the academy project, we do it in the code
        var rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.FINEST);
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.FINEST);
            log.fine("Setting log level for handler: " + handler);
        }

        final var scanner = new Scanner(System.in);
        final var executor = new Executor();
        final var database = new Database<>(1000, "");
        final var controller = new Controller(scanner, executor, database);

        // we'll wrap this in input and provide as dependency injection, but let's
        // do this as standalone component for now
        // even the arch will be a bit retro ... we will use fork join pool
        // but a reactive pattern would be more suitable
        final var server = new Server();
        server.start();

        controller.start();
    }
}
