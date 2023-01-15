package server.interfaces.remote.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.common.Utils;
import server.interfaces.remote.data.Response;

public class RemoteSetCommand implements Command {
    private final Database<String> database;
    private final Exchange exchange;
    private final String sessionId;
    private final String commandParams;

    public RemoteSetCommand(Database<String> database, Exchange exchange, String sessionId, String commandParams) {
        this.database = database;
        this.exchange = exchange;
        this.sessionId = sessionId;
        this.commandParams = commandParams;
    }

    @Override
    public void execute() {
        final var params = Utils.splitOffFirst(commandParams, ' ');
        final var index = Integer.parseInt(params[0]);
        final var result = database.set(index, params[1]);
        if (result.getResponseCode() == ResponseCode.OK) {
            exchange.pushResponse(new Response(sessionId, "Successfully set record: " + commandParams));
        } else {
            exchange.pushResponse(new Response(sessionId, "Failed to set record: " + commandParams));
        }
    }
}
