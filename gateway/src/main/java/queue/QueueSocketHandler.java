package queue;

import config.ConfigProperties;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A WebSocket endpoint to handle the queue
 */
@ServerEndpoint(
        value = "/queue",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class QueueSocketHandler {

    /**
     * A map that holds all sessions of clients currently in queue and the Date object
     * relative to when they joined the queue
     */
    private static final Map<Session, Date> sessions = new ConcurrentHashMap<>();

    private static final Logger LOGGER = Logger.getLogger(QueueSocketHandler.class.getName());

    @OnOpen
    public void onOpen(Session session) {
        // Add a new client to the active sessions with current date
        sessions.put(session, new Date());

        // Notify this new client about current queue size
        Message message = new Message(Message.Type.queue_size, "" + sessions.size());
        sendMessage(message, session);
    }

    @OnClose
    public void onClose(Session session) {
        Date date = sessions.get(session);

        // Remove this client from the active sessions
        sessions.remove(session);

        // When a user is removed from queue, we need to notify all users who joined later
        // to decrease their queue size by one
        Message message = new Message(Message.Type.queue_size_decrease);
        broadcast(message, allSessionsAfterDate(date));
    }

    /**
     * Called when another user has finished the use case, so there is a new slot available
     */
    public static void userFinishedUseCase() {
        // When a user finished use case, we can let first in queue in, if any
        Session first = firstSessionInQueue();
        if (first != null) {
            Message message = new Message(Message.Type.queue_size_decrease);
            sendMessage(message, first);
        }
    }

    /**
     * Sends the specified message to all specified sessions
     *
     * @param message  the message to send
     * @param sessions the sessions list that should receive the message
     */
    private static void broadcast(Message message, List<Session> sessions) {
        sessions.forEach(session -> sendMessage(message, session));
    }

    /**
     * Sends the specified message to the specified session
     *
     * @param message the message to send
     * @param session the session that should receive the message
     */
    private static void sendMessage(Message message, Session session) {
        try {
            session.getAsyncRemote().sendObject(message);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Returns the active session with the least recent joined date
     *
     * @return the first session in queue, null otherwise
     */
    private static Session firstSessionInQueue() {
        return sessions
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
    }

    /**
     * Filters all the active sessions that joined the queue after the specified date
     *
     * @param date the Date to compare to
     * @return list of the filtered sessions
     */
    private static List<Session> allSessionsAfterDate(Date date) {
        return sessions
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().after(date))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * @return true if the maximum number of users inside queue has been reached, false otherwise
     */
    public static Boolean maxQueueSizeReached() {
        // Fallback to this value in case the config.properties file is missing MAX_QUEUE_SIZE property
        int maxQueueSize = 5;

        try {
            maxQueueSize = Integer.parseInt(ConfigProperties.getProperties().getProperty("MAX_QUEUE_SIZE"));
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return sessions.size() >= maxQueueSize;
    }
}
