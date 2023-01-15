package server.interfaces.commands;

public class UnknownCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Unknown command.");
    }
}
