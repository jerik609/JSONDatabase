package server.interfaces;

import server.interfaces.local.commands.Command;

import java.util.LinkedList;
import java.util.Queue;

public class Executor implements Runnable {
    private final Queue<Command> commandQueue = new LinkedList<>();

    public void acceptCommand(Command command) {
        commandQueue.add(command);
    }

    @Override
    public void run() {
        while (!commandQueue.isEmpty()) {
            commandQueue.remove().execute();
        }
    }
}