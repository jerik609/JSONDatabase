package server.input;

import server.database.Database;

import java.util.Scanner;

public class Controller {
    private final Scanner scanner;
    private final Executor executor;
    private final CommandFactory commandFactory;
    private boolean stop;

    public Controller(Scanner scanner, Executor executor, Database<String> database) {
        this.scanner = scanner;
        this.executor = executor;
        stop = false;
        commandFactory = new CommandFactory(this, database);
    }

    public void start() {
        do {
            final var input = scanner.nextLine();
            String[] commandArray = Utils.splitOffFirst(input, ' ');
            executor.acceptCommand(commandFactory.getCommandFromAction(
                    Action.from(commandArray[0]),
                    commandArray[1]));
            executor.execute();
        } while (!stop);
    }

    public void stop() {
        stop = true;
    }
}