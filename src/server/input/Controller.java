package server.input;

import server.database.Database;

import java.util.Arrays;
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

    public void run() {
        do {
            final var input = scanner.nextLine();
            String[] commandArray = input.split(" ");
            executor.acceptCommand(commandFactory.getCommandFromAction(
                    Action.from(commandArray[0]),
                    Arrays.copyOfRange(commandArray, 1, commandArray.length)));
            executor.execute();
        } while (!stop);
    }

    public void stop() {
        stop = true;
    }
}