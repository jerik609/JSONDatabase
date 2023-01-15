package server.interfaces.remote.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.remote.data.Response;

import java.util.logging.Logger;

public class RemoteDeleteCommand implements Command {
    private static final Logger log = Logger.getLogger(RemoteDeleteCommand.class.getSimpleName());

    private final Database<String> database;
    private final Exchange exchange;
    private final String sessionId;
    private final String commandParams;

    public RemoteDeleteCommand(Database<String> database, Exchange exchange, String sessionId, String commandParams) {
        this.database = database;
        this.exchange = exchange;
        this.sessionId = sessionId;
        this.commandParams = commandParams;
    }

    @Override
    public void execute() {
        log.fine("Executing command for " + commandParams);
        final var index = Integer.parseInt(commandParams);
        final var result = database.delete(index);
        if (result.getResponseCode() == ResponseCode.OK) {
            log.fine("Success for: " + commandParams);
            exchange.pushResponse(new Response(sessionId, "OK"));
        } else {
            log.fine("Failed for: " + commandParams);
            exchange.pushResponse(new Response(sessionId, "ERROR"));
        }
        log.fine("Pushed response for result: " + result);
    }
}
