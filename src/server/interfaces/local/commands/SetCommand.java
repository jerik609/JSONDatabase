package server.interfaces.local.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;
import server.interfaces.common.Utils;

public class SetCommand implements Command {
    private final Database<String> database;
    private final String commandParams;

    public SetCommand(Database<String> database, String commandParams) {
        this.database = database;
        this.commandParams = commandParams;
    }

    @Override
    public void execute() {
        final var params = Utils.splitOffFirst(commandParams, ' ');
        final var index = params[0];
        final var result = database.set(index, params[1]);
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }
}
