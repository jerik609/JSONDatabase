package server.input.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.input.Command;

public class DeleteCommand implements Command {
    private final Database<String> database;
    private final String[] commandStr;

    public DeleteCommand(Database<String> database, String[] commandStr) {
        this.database = database;
        this.commandStr = commandStr;
    }

    @Override
    public void execute() {
        final var result = database.delete(commandStr[0]);
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }
}
