package concurrent_users;

import config.ConfigProperties;
import queue.MessageDecoder;
import queue.MessageEncoder;
import queue.QueueSocketHandler;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A WebSocket endpoint to handle all concurrent users using the "critical" part of the service
 */
@ServerEndpoint(
        value = "/concurrent-users",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class ConcurrentUsersSocketHandler {

    /**
     * A set that holds the sessions of all client currently using the "critical" service
     */
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    private static final Logger LOGGER = Logger.getLogger(ConcurrentUsersSocketHandler.class.getName());

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("New user started use case! -> " + session.getId());
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.info("User ended use case! -> " + session.getId());
        sessions.remove(session);
        // Also notify queue socket handler, to let first in queue in
        QueueSocketHandler.userFinishedUseCase();
    }

    /**
     * @return true if the maximum number of concurrent users has been reached, false otherwise
     */
    public static Boolean maxUsersReached() {
        // Fallback to this value in case the config.properties file is missing MAX_CONCURRENT_USERS property
        int maxConcurrentUsers = 5;
        
        try {
            maxConcurrentUsers = Integer.parseInt(ConfigProperties.getProperties().getProperty("MAX_CONCURRENT_USERS"));
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return sessions.size() >= maxConcurrentUsers;
    }

}
