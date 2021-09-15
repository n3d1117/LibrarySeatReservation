package rsocket;

import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RSocketTest {

    private static CloseableChannel channel;
    private static final int port = 7878;
    private static final String setupMessage = "setup";

    @BeforeEach
    public void startServer() {
        channel = RSocketServer
                .create(new RSocketClientHandler())
                .bindNow(WebsocketServerTransport.create(port));
        channel.onClose();
    }

    @AfterEach
    public void stopServer() {
        channel.dispose();
    }

    @Test
    public void testClientConnectionSuccessful() {
        assertDoesNotThrow(() ->
                RSocketConnector.create()
                        .connect(WebsocketClientTransport.create(port))
                        .block()
        );
    }

    @Test
    public void testClientSetup() {
        List<String> expected = Collections.singletonList(setupMessage);
        List<String> receivedMessages = new ArrayList<>();

        RSocket client = RSocketConnector.create()
                .acceptor(SocketAcceptor.forFireAndForget(payload -> {
                    receivedMessages.add(payload.getDataUtf8());
                    return Mono.empty();
                }))
                .connect(WebsocketClientTransport.create(port))
                .block();
        assertNotNull(client);

        client.fireAndForget(DefaultPayload.create(setupMessage)).block();

        await().atMost(5, SECONDS).until(() -> receivedMessages.equals(expected));
    }

    @Test
    public void testNotify() throws InterruptedException {
        List<String> expected = Arrays.asList("this", "is", "a", "test");
        List<String> receivedMessages = new ArrayList<>();

        connectClientAndSetup(receivedMessages);

        for (String message: expected) {
            Thread.sleep(500);
            RSocketClientHandler.notifyAll(message);
        }

        await().atMost(5, SECONDS).until(() -> receivedMessages.equals(expected));
    }

    @Test
    public void testBroadcastMultipleClients() throws InterruptedException {
        List<String> expected = Arrays.asList("this", "is", "a", "test");
        List<String> receivedMessages = new ArrayList<>();
        List<String> receivedMessages2 = new ArrayList<>();

        connectClientAndSetup(receivedMessages);
        connectClientAndSetup(receivedMessages2);

        for (String message: expected) {
            Thread.sleep(500);
            RSocketClientHandler.notifyAll(message);
        }

        await().atMost(5, SECONDS).until(
                () -> receivedMessages.equals(expected) && receivedMessages2.equals(expected)
        );
    }

    private void connectClientAndSetup(List<String> receivedMessages) {
        RSocketConnector.create()
                .acceptor(SocketAcceptor.forFireAndForget(payload -> {
                    if (!payload.getDataUtf8().equals(setupMessage))
                        receivedMessages.add(payload.getDataUtf8());
                    return Mono.empty();
                }))
                .connect(WebsocketClientTransport.create(port))
                .block()
                .fireAndForget(DefaultPayload.create(setupMessage))
                .block();
    }
}
