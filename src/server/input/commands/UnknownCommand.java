package server.input.commands;

import server.input.Command;

public class UnknownCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Unknown command.");
    }
}
