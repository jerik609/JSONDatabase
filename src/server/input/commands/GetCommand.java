package server.input.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.input.Command;

public class GetCommand implements Command {
    private final Database<String> database;
    private final String[] commandStr;

    public GetCommand(Database<String> database, String[] commandStr) {
        this.database = database;
        this.commandStr = commandStr;
    }

    @Override
    public void execute() {
        final var result = database.get(commandStr[0]);
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println(result.getData());
        } else {
            System.out.println("ERROR");
        }
    }
}
