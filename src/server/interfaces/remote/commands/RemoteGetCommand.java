package server.interfaces.remote.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.remote.data.Response;

public class RemoteGetCommand implements Command {
    private final Database<String> database;
    private final Exchange exchange;
    private final String sessionId;
    private final String commandParams;

    public RemoteGetCommand(Database<String> database, Exchange exchange, String sessionId, String commandParams) {
        this.database = database;
        this.exchange = exchange;
        this.sessionId = sessionId;
        this.commandParams = commandParams;
    }

    @Override
    public void execute() {
        final var index = Integer.parseInt(commandParams);
        final var result = database.get(index);
        if (result.getResponseCode() == ResponseCode.OK) {
            exchange.pushResponse(new Response(
                            sessionId,
                            "The data: " + result.getData().orElseThrow(
                                    () -> new RuntimeException("empty result despite success!?"))));
        } else {
            exchange.pushResponse(new Response(sessionId, "Failed to get data for: " + commandParams));
        }
    }
}