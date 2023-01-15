package server.interfaces.remote.commands;

import server.interfaces.Command;
import server.interfaces.Exchange;
import server.interfaces.remote.data.Response;

import java.util.concurrent.atomic.AtomicBoolean;

public class RemoteExitCommand implements Command {
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
        exchange.pushResponse(new Response(sessionId, "OK"));
        stop.getAndSet(true);
    }
}
