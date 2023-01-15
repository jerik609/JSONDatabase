package server.interfaces.local.commands;

import server.interfaces.remote.data.Request;

public class ExternalGetCommand implements Command {
    private final Request request;

    public ExternalGetCommand(Request request) {
        this.request = request;
    }

    @Override
    public void execute() {

    }
}
