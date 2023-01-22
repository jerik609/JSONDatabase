package server.interfaces.local.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.common.Utils;

import java.util.logging.Logger;

public class SetCommand implements Command {
    private final static Logger log = Logger.getLogger(SetCommand.class.getSimpleName());

    private static final Gson gson = new GsonBuilder().create();

    private final Database database;
    private final String commandParams;

    public SetCommand(Database database, String commandParams) {
        this.database = database;
        this.commandParams = commandParams;
    }

    @Override
    public void execute() {
        final var params = Utils.splitOffFirst(commandParams, ' ');
        final var index = params[0];
        final var jsonObject = gson.fromJson(params[1], JsonObject.class);
        log.fine("Local command: " + index + ", " + jsonObject);
        final var result = database.set(index, jsonObject);
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }
}
