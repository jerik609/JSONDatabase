package server.interfaces.remote.commands;

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

import static common.Message.MESSAGE_KEY_FIELD;

public class RemoteDeleteCommand implements Command {
    private static final Logger log = Logger.getLogger(RemoteDeleteCommand.class.getSimpleName());

    private final Database database;
    private final Exchange exchange;
    private final String sessionId;
    private final JsonObject payload;

    public RemoteDeleteCommand(Database database, Exchange exchange, String sessionId, JsonObject payload) {
        this.database = database;
        this.exchange = exchange;
        this.sessionId = sessionId;
        this.payload = payload;
    }

    @Override
    public void execute() {
        log.fine("Executing command for " + payload);

        final var key = payload.getAsJsonPrimitive(MESSAGE_KEY_FIELD);
        if (key == null) {
            throw new RuntimeException("Payload does not contain a key: " + payload);
        }

        final var result = database.delete(key.getAsString());
        if (result.getResponseCode() == ResponseCode.OK) {
            log.fine("Success for: " + payload);
            exchange.pushResponse(new Response(sessionId, new OkRemoteResponse()));
        } else {
            log.fine("Failed for: " + payload);
            exchange.pushResponse(new Response(sessionId, new ErrorRemoteResponse("No such key")));
        }
        log.fine("Pushed response for result: " + result);
    }
}
