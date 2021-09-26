package queue;

import com.google.gson.Gson;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * JSON encodes a Message object
 */
public class MessageEncoder implements Encoder.Text<Message> {

    private static final Gson gson = new Gson();

    @Override
    public String encode(Message message) {
        return gson.toJson(message);
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }
}
