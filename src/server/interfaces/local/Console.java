package server.interfaces.local;

import server.interfaces.Executor;
import server.interfaces.common.Action;
import server.interfaces.common.Utils;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Console {
    private static final Logger log = Logger.getLogger(Console.class.getSimpleName());

    private final AtomicBoolean stop;
    private final Scanner scanner;
    private final LocalCommandFactory localCommandFactory;
    private final Executor executor;

    public Console(AtomicBoolean stop, Scanner scanner, LocalCommandFactory localCommandFactory, Executor executor) {
        this.stop = stop;
        this.scanner = scanner;
        this.localCommandFactory = localCommandFactory;
        this.executor = executor;
    }

    public void start() {
        log.fine("Started.");
        System.out.println("Enter command:");
        do {
            final var input = scanner.nextLine();
            String[] commandArray = Utils.splitOffFirst(input, ' ');
            executor.acceptCommand(localCommandFactory.getCommandFromAction(
                    Action.from(commandArray[0]),
                    commandArray[1]));
            executor.run();
        } while (!stop.get());
        log.fine("Stopped.");
    }
}
