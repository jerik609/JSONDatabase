package server.interfaces.remote.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.remote.data.Response;

public class RemoteDeleteCommand implements Command {
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
        final var index = Integer.parseInt(commandParams);
        final var result = database.delete(index);
        if (result.getResponseCode() == ResponseCode.OK) {
            exchange.pushResponse(new Response(sessionId, "Successfully deleted data for: " + commandParams));
        } else {
            exchange.pushResponse(new Response(sessionId, "Failed to delete data for: " + commandParams));
        }
    }
}
