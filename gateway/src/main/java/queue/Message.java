package queue;

/**
 * Represent a Socket message used for client-server communication over WebSockets
 */
public class Message {

    /**
     * The possible message types:
     * - queue_size: specifies current queue size
     * - queue_size_decrease: tells the client to decrease queue size by one
     */
    enum Type {
        queue_size,
        queue_size_decrease
    }

    private final Type type;
    private final String value;

    public Message(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Message(Type type) {
        this.type = type;
        this.value = "";
    }
}
