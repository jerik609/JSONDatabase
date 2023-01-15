package server.input.command;

import server.Controller;

import java.util.logging.Logger;

public class ExitCommand implements Command {
    private static final Logger log = Logger.getLogger(ExitCommand.class.getSimpleName());

    private final Controller controller;

    public ExitCommand(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void execute() {
        log.fine("Executing stop command.");
        controller.stop();
    }
}
