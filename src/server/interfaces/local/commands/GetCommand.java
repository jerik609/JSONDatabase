package server.interfaces.local.commands;

import server.database.Database;
import server.database.ResponseCode;
import server.interfaces.Command;

public class GetCommand implements Command {
    private final Database database;
    private final String commandParams;

    public GetCommand(Database database, String commandParams) {
        this.database = database;
        this.commandParams = commandParams;
    }

    @Override
    public void execute() {
        final var index = commandParams;
        final var result = database.get(new String[]{index});
        if (result.getResponseCode() == ResponseCode.OK) {
            System.out.println(result.getData()
                    .orElseThrow(() -> new RuntimeException("empty result despite success!?")));
        } else {
            System.out.println("ERROR");
        }
    }
}
