package server.interfaces.remote.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.Exchange;
import common.response.Response;

import java.util.Arrays;
import java.util.logging.Logger;

import static common.Message.getKeyAsArrays;

public class RemoteGetCommand implements Command {
    private static final Logger log = Logger.getLogger(RemoteGetCommand.class.getSimpleName());

    private static final Gson gson = new GsonBuilder().create();

    private final Database database;
    private final Exchange exchange;
    private final String sessionId;
    private final JsonObject payload;

    public RemoteGetCommand(Database database, Exchange exchange, String sessionId, JsonObject payload) {
        this.database = database;
        this.exchange = exchange;
        this.sessionId = sessionId;
        this.payload = payload;
    }

    @Override
    public void execute() {
        final var keys = getKeyAsArrays(payload);

        log.fine("Executing command for: " + payload + ", using keys: " + Arrays.toString(keys));

        final var result = database.get(keys);

        final var responseJsonObj = new JsonObject();
        if (result.getResponseCode() == ResponseCode.OK) {
            log.fine("Success for: " + payload);
            responseJsonObj.add("response", new JsonPrimitive("OK"));
            responseJsonObj.add("value", result.getData().orElseThrow(
                    () -> new RuntimeException("Database query was successful, but returned no data.")));
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
