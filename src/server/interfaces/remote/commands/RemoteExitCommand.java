package server.interfaces.remote.commands;

import common.response.OkRemoteResponse;
import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.remote.data.Response;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class RemoteExitCommand implements Command {
    private static final Logger log = Logger.getLogger(RemoteExitCommand.class.getSimpleName());

    private final AtomicBoolean stop;
    private final Exchange exchange;
    private final String sessionId;

    public RemoteExitCommand(AtomicBoolean stop, Exchange exchange, String sessionId) {
        this.stop = stop;
        this.exchange = exchange;
        this.sessionId = sessionId;
    }

    @Override
    public void execute() {
        log.fine("Executing command");
        exchange.pushResponse(new Response(sessionId, new OkRemoteResponse()));
        stop.getAndSet(true);
        log.fine("Done executing command");
    }
}
