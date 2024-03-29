package server.interfaces.local;

import server.database.Database;
import server.interfaces.Command;
import server.interfaces.common.Action;
import server.interfaces.local.commands.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class LocalCommandFactory {
    private final AtomicBoolean stop;
    private final Database database;

    public LocalCommandFactory(AtomicBoolean stop, Database database) {
        this.stop = stop;
        this.database = database;
    }

    public Command getCommandFromAction(Action action, String commandParams) {
        return switch (action) {
            case SET -> new SetCommand(database, commandParams);
            case GET -> new GetCommand(database, commandParams);
            case DELETE -> new DeleteCommand(database, commandParams);
            case EXIT -> new ExitCommand(stop);
            default -> new UnknownCommand();
        };
    }
}
