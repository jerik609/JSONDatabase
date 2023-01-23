package server.interfaces.remote.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.Exchange;
import common.Response;

import java.util.Arrays;
import java.util.logging.Logger;

import static common.Message.getKeyAsArrays;

public class RemoteSetCommand implements Command {
    private static final Logger log = Logger.getLogger(RemoteSetCommand.class.getSimpleName());

    private final Database database;
    private final Exchange exchange;
    private final String sessionId;
    private final JsonObject payload;

    public RemoteSetCommand(Database database, Exchange exchange, String sessionId, JsonObject payload) {
        this.database = database;
        this.exchange = exchange;
        this.sessionId = sessionId;
        this.payload = payload;
        log.fine("Created command.");
    }

    @Override
    public void execute() {
        final var keys = getKeyAsArrays(payload);

        log.fine("Executing command for: " + payload + ", using keys: " + Arrays.toString(keys));

        final var result = database.set(keys, payload);

        final var responseJsonObj = new JsonObject();
        if (result.getResponseCode() == ResponseCode.OK) {
            log.fine("Success for: " + payload);
            responseJsonObj.add("response", new JsonPrimitive("OK"));
            exchange.pushResponse(new Response(sessionId, responseJsonObj));
        } else {
            log.fine("failed for: " + payload);
            responseJsonObj.add("response", new JsonPrimitive("ERROR"));
            responseJsonObj.add("reason", new JsonPrimitive("No such key"));
            exchange.pushResponse(new Response(sessionId, responseJsonObj));
        }

        log.fine("Pushed response for result: " + result);
    }
}
