package server.input.command;

import server.database.Database;
import server.database.ResponseCode;
import server.input.common.Utils;

public class SetCommand implements Command {
    private final Database<String> database;
    private final String commandStr;

    public SetCommand(Database<String> database, String commandParams) {
        this.database = database;
        this.commandStr = commandParams;
    }

    @Override
    public void execute() {
        final var params = Utils.splitOffFirst(commandStr, ' ');
        final var index = Integer.parseInt(params[0]);
        final var result = database.set(index, params[1]);
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }
}