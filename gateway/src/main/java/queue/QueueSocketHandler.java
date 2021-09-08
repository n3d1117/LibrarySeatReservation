package queue;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ServerEndpoint(
        value = "/queue",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class QueueSocketHandler {

    private static final Map<Session, Date> sessions = new ConcurrentHashMap<>();
    private static final Logger LOGGER = Logger.getLogger(QueueSocketHandler.class.getName());

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("QUEUE: ADD USER " + session.getId());
        sessions.put(session, new Date());

        // notify new user about current queue size
        Message message = new Message("queue_size", size().toString());
        sendMessage(message, session);
    }

    @OnClose
    public void onClose(Session session) {

        Date d = sessions.get(session);

        LOGGER.info("QUEUE: REMOVE USER " + session.getId());
        sessions.remove(session);

        // when a user is removed from queue, we need to notify all users who joined later
        LOGGER.info("QUEUE: NOTIFY ALL " + allSessionsAfterDate(d).size() + " USERS WHO JOINED LATER");
        Message message = new Message("queue_size_decrease", "");
        broadcast(message, allSessionsAfterDate(d));
    }

    public static void userFinishedUseCase() {
        // when a user finished use case, we can let first in queue in, if any
        Session first = firstSessionInQueue();
        if (first != null) {
            LOGGER.info("QUEUE: userFinishedUseCase, LET FIRST IN QUEUE IN: " + first.getId());
            Message message = new Message("queue_size_decrease", "");
            sendMessage(message, first);
        }
    }

    private Integer size() {
        return sessions.size();
    }

    private static void broadcast(Message message, List<Session> sessions) {
        sessions.forEach(session -> sendMessage(message, session));
    }

    private static void sendMessage(Message message, Session session) {
        try {
            session.getBasicRemote().sendObject(message);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    private static Session firstSessionInQueue() {
        return sessions
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
    }

    private static List<Session> allSessionsAfterDate(Date date) {
        return sessions
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().after(date))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}