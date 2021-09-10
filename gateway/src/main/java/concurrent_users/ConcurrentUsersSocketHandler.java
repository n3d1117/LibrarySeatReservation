package concurrent_users;

import queue.MessageDecoder;
import queue.MessageEncoder;
import queue.QueueSocketHandler;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(
        value = "/concurrent-users",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class ConcurrentUsersSocketHandler {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        // also notify queue socket handler to let first in queue in
        QueueSocketHandler.userFinishedUseCase();
    }

    public static Boolean maxUsersReached() {
        return sessions.size() >= 5; // todo parametrize max users number
    }

}
