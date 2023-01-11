package server.input.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.input.Command;
import server.input.Utils;

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
        final var result = database.set(params[0], params[1]);
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }
}
