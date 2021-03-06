package startup;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import reactor.core.publisher.Hooks;
import rsocket.RSocketNotifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Logger;

/**
 * This RSocket server is active on port 7878 and used to communicate changes in the database
 * in real-time with any connected RSocket client on the fronted side (i.e. admins with an open dashbaord).
 * This class is wrapped in a @Singleton @Startup so that it starts and stops in sync with the Java application.
 */
@Singleton
@Startup
@DependsOn("StartupBean")
public class AdminRSocketServer {

    private static CloseableChannel channel;
    private static final Logger LOGGER = Logger.getLogger(AdminRSocketServer.class.getName());

    @PostConstruct
    public void init() {
        LOGGER.info("Starting admin RSocket server on port 7878...");
        Hooks.onErrorDropped(error -> {});
        channel = RSocketServer
                .create(new RSocketNotifier())
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .bindNow(WebsocketServerTransport.create(7878));
        channel.onClose();
    }

    @PreDestroy
    protected void dispose() {
        channel.dispose();
    }

}
