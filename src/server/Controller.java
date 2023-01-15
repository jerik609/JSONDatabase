package server;

import server.database.Database;
import server.input.Executor;
import server.input.command.Action;
import server.input.command.CommandFactory;
import server.input.common.Utils;

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
            executor.run();
        } while (!stop);
        System.out.println("controlled has stopped");
    }

    public void stop() {
        stop = true;
    }
}