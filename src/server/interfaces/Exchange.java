package server.interfaces;

import server.interfaces.remote.Session;
import server.interfaces.remote.data.Request;
import common.response.Response;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * Just a placeholder for communication "queues".
 * Can be called from multiple threads.
 */
public class Exchange {
    private static final Logger log = Logger.getLogger(Exchange.class.getSimpleName());

    /* FIFO queue for client requests */
    private final ConcurrentLinkedQueue<Request> requestQueue = new ConcurrentLinkedQueue<>();
    /* Map of FIFO queues for client responses, mapping by sessionId */
    // TODO: we should have a process which removes old data of failed sessions
    private final ConcurrentMap<String, ConcurrentLinkedQueue<Response>> responseMap = new ConcurrentHashMap<>();
    /* map of all sessions by session ids */
    private final ConcurrentLinkedQueue<Session> sessions = new ConcurrentLinkedQueue<>();

    /**
     * Enqueue request to the tail of the request queue.
     * @param request
     */
    public void pushRequest(Request request) {
        requestQueue.add(request);
    }

    /**
     * Take (remove) request from the head of the request queue.
     * @return optional of Request
     */
    public Optional<Request> takeRequest() {
        try {
            return Optional.of(requestQueue.remove());
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    /**
     * Enqueue response to the tail of the respective response queue within the response map.
     * If such response queue does not exist, create it.
     * @param response
     */
    public void pushResponse(Response response) {
        responseMap.computeIfPresent(response.sessionId(), (key, queue) -> {
            queue.add(response);
            return queue;
        });
        responseMap.computeIfAbsent(response.sessionId(), key -> {
            final var queue = new ConcurrentLinkedQueue<Response>();
            queue.add(response);
            return queue;
        });
    }

    /**
     * Take (remove) response from the head of the respective response queue in the response map.
     * @param sessionId
     * @return optional of Response
     */
    public Optional<Response> takeResponse(String sessionId) {
        if (!responseMap.containsKey(sessionId)) {
            return Optional.empty();
        }
        try {
            return Optional.of(responseMap.get(sessionId).remove());
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public boolean hasPendingResponses() {
        return responseMap.entrySet().stream().anyMatch(entry -> !entry.getValue().isEmpty());
    }

    /**
     * Cleanup all data associated with the provided session ID.
     * @param sessionId
     */
    public void cleanUp(String sessionId) {
        log.fine("Removing all data for session: " + sessionId);
        requestQueue.removeIf(request -> request.sessionId().equals(sessionId));
        responseMap.remove(sessionId);
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public ConcurrentLinkedQueue<Session> getSessions() {
        return sessions;
    }
}
