package server.interfaces.remote.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.common.Utils;
import server.interfaces.remote.data.Response;

import java.util.Arrays;
import java.util.logging.Logger;

public class RemoteSetCommand implements Command {
    private static final Logger log = Logger.getLogger(RemoteSetCommand.class.getSimpleName());

    private final Database<String> database;
    private final Exchange exchange;
    private final String sessionId;
    private final String[] commandParams;

    public RemoteSetCommand(Database<String> database, Exchange exchange, String sessionId, String[] commandParams) {
        this.database = database;
        this.exchange = exchange;
        this.sessionId = sessionId;
        this.commandParams = commandParams;
        log.fine("Created command.");
    }

    @Override
    public void execute() {
        log.fine("Executing command for " + Arrays.toString(commandParams));
        final var index = Integer.parseInt(commandParams[0]);
        final var result = database.set(index, commandParams[1]);
        if (result.getResponseCode() == ResponseCode.OK) {
            log.fine("Success for: " + Arrays.toString(commandParams));
            exchange.pushResponse(new Response(sessionId, "OK"));
        } else {
            log.fine("Failed for: " + Arrays.toString(commandParams));
            exchange.pushResponse(new Response(sessionId, "ERROR"));
        }
        log.fine("Pushed response for result: " + result);
    }
}
