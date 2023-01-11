package server.input.commands;

import server.input.Command;
import server.input.Controller;

public class ExitCommand implements Command {
    private final Controller controller;

    public ExitCommand(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void execute() {
        controller.stop();
    }
}
