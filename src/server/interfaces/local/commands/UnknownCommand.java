package server.interfaces.local.commands;

import server.interfaces.Command;

public class UnknownCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Unknown command.");
    }
}
