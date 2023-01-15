package server.input.command;

import server.database.Database;
import server.Controller;

public class CommandFactory {
    private final Controller controller;
    private final Database<String> database;

    public CommandFactory(Controller controller, Database<String> database) {
        this.controller = controller;
        this.database = database;
    }

    public Command getCommandFromAction(Action action, String commandParams) {
        return switch (action) {
            case UNKNOWN -> new UnknownCommand();
            case SET -> new SetCommand(database, commandParams);
            case GET -> new GetCommand(database, commandParams);
            case DELETE -> new DeleteCommand(database, commandParams);
            case EXIT -> new ExitCommand(controller);
        };
    }
}
