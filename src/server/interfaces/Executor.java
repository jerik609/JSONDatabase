package server.interfaces;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public class Executor implements Runnable {
    private static final Logger log = Logger.getLogger(Executor.class.getSimpleName());

    private final Queue<Command> commandQueue = new LinkedList<>();

    public void acceptCommand(Command command) {
        commandQueue.add(command);
    }

    @Override
    public void run() {
        try {
            while (!commandQueue.isEmpty()) {
                commandQueue.remove().execute();
            }
        } catch (Exception e) {
            log.severe("Unexpected exception when executing command: " + e);
            e.printStackTrace();
        }
    }
}