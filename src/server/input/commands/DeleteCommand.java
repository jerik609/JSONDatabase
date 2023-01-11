package server.input.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.input.Command;

public class DeleteCommand implements Command {
    private final Database<String> database;
    private final String commandParams;

    public DeleteCommand(Database<String> database, String commandParams) {
        this.database = database;
        this.commandParams = commandParams;
    }

    @Override
    public void execute() {
        final var index = Integer.parseInt(commandParams);
        final var result = database.delete(index);
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }
}
