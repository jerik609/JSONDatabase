package server.interfaces.remote.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import common.response.ErrorRemoteResponse;
import common.response.OkRemoteResponse;
import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.remote.data.Response;

import java.util.Arrays;
import java.util.logging.Logger;

public class RemoteSetCommand implements Command {
    private static final Logger log = Logger.getLogger(RemoteSetCommand.class.getSimpleName());

    private static final Gson gson = new GsonBuilder().create();

    private final Database database;
    private final Exchange exchange;
    private final String sessionId;
    private final String[] commandParams;

    public RemoteSetCommand(Database database, Exchange exchange, String sessionId, String[] commandParams) {
        this.database = database;
        this.exchange = exchange;
        this.sessionId = sessionId;
        this.commandParams = commandParams;
        log.fine("Created command.");
    }

    @Override
    public void execute() {
        log.fine("Executing command for " + Arrays.toString(commandParams));
        final var index = commandParams[0];
        final var jsonObject = gson.fromJson(commandParams[1], JsonObject.class);
        log.fine("Local command: " + index + ", " + jsonObject);
        final var result = database.set(index, jsonObject);
        if (result.getResponseCode() == ResponseCode.OK) {
            log.fine("Success for: " + Arrays.toString(commandParams));
            exchange.pushResponse(new Response(sessionId, new OkRemoteResponse()));
        } else {
            log.fine("Failed for: " + Arrays.toString(commandParams));
            exchange.pushResponse(new Response(sessionId, new ErrorRemoteResponse("No such key")));
        }
        log.fine("Pushed response for result: " + result);
    }
}
