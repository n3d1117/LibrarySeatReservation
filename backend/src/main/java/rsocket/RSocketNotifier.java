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

    public static void notifyAll(String message) {
        Payload payload = DefaultPayload.create(message);
        clients.forEach(client -> client.fireAndForget(payload).block());
    }
}
