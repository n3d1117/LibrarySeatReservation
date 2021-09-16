package queue;

public class Message {

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
