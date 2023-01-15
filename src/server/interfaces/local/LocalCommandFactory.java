package server.interfaces.local;

import server.database.Database;
import server.interfaces.common.Action;
import server.interfaces.local.commands.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class LocalCommandFactory {
    private final AtomicBoolean stop;
    private final Database<String> database;

    public LocalCommandFactory(AtomicBoolean stop, Database<String> database) {
        this.stop = stop;
        this.database = database;
    }

    public Command getCommandFromAction(Action action, String commandParams) {
        return switch (action) {
            case UNKNOWN -> new UnknownCommand();
            case SET -> new SetCommand(database, commandParams);
            case GET -> new GetCommand(database, commandParams);
            case DELETE -> new DeleteCommand(database, commandParams);
            case EXIT -> new ExitCommand(stop);
        };
    }
}
