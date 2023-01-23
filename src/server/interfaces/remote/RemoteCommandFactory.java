package server.interfaces.remote;

import com.google.gson.JsonObject;
import server.database.Database;
import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.common.Action;
import server.interfaces.local.commands.*;
import server.interfaces.remote.commands.RemoteDeleteCommand;
import server.interfaces.remote.commands.RemoteExitCommand;
import server.interfaces.remote.commands.RemoteGetCommand;
import server.interfaces.remote.commands.RemoteSetCommand;

import java.util.concurrent.atomic.AtomicBoolean;

public class RemoteCommandFactory {
    private final AtomicBoolean stop;
    private final Database database;
    private final Exchange exchange;

    public RemoteCommandFactory(AtomicBoolean stop, Database database, Exchange exchange) {
        this.stop = stop;
        this.database = database;
        this.exchange = exchange;
    }

    public Command getRemoteCommandFromRequest(String sessionId, Action action, JsonObject payload) {
        return switch (action) {
            case SET -> new RemoteSetCommand(database, exchange, sessionId, payload);
            case GET -> new RemoteGetCommand(database, exchange, sessionId, payload);
            case DELETE -> new RemoteDeleteCommand(database, exchange, sessionId, payload);
            case EXIT -> new RemoteExitCommand(stop, exchange, sessionId);
            default -> new UnknownCommand();
        };
    }
}
