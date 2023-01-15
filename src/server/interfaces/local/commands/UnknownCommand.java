package server.interfaces.local.commands;

public class UnknownCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Unknown command.");
    }
}
