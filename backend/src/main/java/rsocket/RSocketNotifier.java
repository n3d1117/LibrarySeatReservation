package rsocket;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A SocketAcceptor implementation that saves all listening RSocket clients into a set
 * and allows of broadcasting messages to all of them simultaneously.
 */
public class RSocketNotifier implements SocketAcceptor {

    private static final Set<RSocket> clients = Collections.synchronizedSet(new HashSet<>());
    private static final Logger LOGGER = Logger.getLogger(RSocketNotifier.class.getName());

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
        sendingSocket
                .onClose()
                .doFirst(() -> {
                    LOGGER.info("New RSocket client connected!");
                    clients.add(sendingSocket);
                })
                .doOnError(error -> LOGGER.warning("Error in RSocket: " + error))
                .doFinally(consumer -> {
                    LOGGER.info("RSocket client disconnected!");
                    clients.remove(sendingSocket);
                })
                .subscribe();
        return Mono.just(sendingSocket);
    }

    /**
     * Send a FNF (Fire and Forget) message to every listening client
     * See also https://rsocket.io/about/motivations#fire-and-forget
     * @param message the message to broadcast
     */
    public static void notifyAll(String message) {
        Payload payload = DefaultPayload.create(message);
        clients.forEach(client -> client.fireAndForget(payload).block());
    }
}
