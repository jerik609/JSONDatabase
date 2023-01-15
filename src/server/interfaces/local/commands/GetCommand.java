package server.interfaces.local.commands;

import server.database.Database;
import server.database.ResponseCode;

public class GetCommand implements Command {
    private final Database<String> database;
    private final String commandParams;

    public GetCommand(Database<String> database, String commandParams) {
        this.database = database;
        this.commandParams = commandParams;
    }

    @Override
    public void execute() {
        final var index = Integer.parseInt(commandParams);
        final var result = database.get(index);
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println(result.getData()
                    .orElseThrow(() -> new RuntimeException("empty result despite success!?")));
        } else {
            System.out.println("ERROR");
        }
    }
}
