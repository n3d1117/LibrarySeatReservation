package concurrent_users;

import queue.MessageDecoder;
import queue.MessageEncoder;
import queue.QueueSocketHandler;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@ServerEndpoint(
        value = "/concurrent-users",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class ConcurrentUsersSocketHandler {

    private static final Map<Session, Date> sessions = new ConcurrentHashMap<>();
    private static final Logger LOGGER = Logger.getLogger(ConcurrentUsersSocketHandler.class.getName());

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("USERS: NEW USER STARTED USE CASE: " + session.getId());
        sessions.put(session, new Date());
        LOGGER.info("CURRENT USERS SIZE: " + sessions.size());
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.info("USERS: USER FINISHED USE CASE: " + session.getId());
        endUseCase(session);
        LOGGER.info("CURRENT USERS SIZE: " + sessions.size());
    }

    public static Boolean maxUsersReached() {
        LOGGER.info("CHECK CURRENT USERS SIZE: " + sessions.size());
        return sessions.size() >= 5; // todo parametrize max users number
    }

    private static void endUseCase(Session session) {
        sessions.remove(session);
        // also notify queue socket handler to update queue
        QueueSocketHandler.userFinishedUseCase();
    }

    /*private static Boolean isExpired(Date date) {
        Date now = new Date();
        long maxDuration = MILLISECONDS.convert(2, MINUTES); // todo parametrize minutes
        return now.getTime() - date.getTime() >= maxDuration;
    }*/

}
