package server.interfaces.local.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;

public class DeleteCommand implements Command {
    private final Database database;
    private final String commandParams;

    public DeleteCommand(Database database, String commandParams) {
        this.database = database;
        this.commandParams = commandParams;
    }

    @Override
    public void execute() {
        final var index = commandParams;
        final var result = database.delete(index);
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }
}
