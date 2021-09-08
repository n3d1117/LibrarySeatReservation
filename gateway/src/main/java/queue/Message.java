package queue;

public class Message {

    private final String action;
    private final String value;

    public Message(String action, String value) {
        this.action = action;
        this.value = value;
    }
}
