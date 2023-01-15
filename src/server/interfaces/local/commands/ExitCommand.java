package server.interfaces.local.commands;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class ExitCommand implements Command {
    private static final Logger log = Logger.getLogger(ExitCommand.class.getSimpleName());

    private final AtomicBoolean stop;

    public ExitCommand(AtomicBoolean stop) {
        this.stop = stop;
    }

    @Override
    public void execute() {
        log.fine("Issuing stop command.");
        stop.getAndSet(true);
    }
}
