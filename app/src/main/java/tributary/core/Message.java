package tributary.core;

import java.time.LocalDateTime;

import tributary.api.IMessage;

public class Message<T> implements IMessage<T> {
    private LocalDateTime created;
    private String id;
    private String payloadType;
    private String key;
    private T value;

    public Message(String id, String payloadType, String key, T value) {
        this.created = LocalDateTime.now();
        this.id = id;
        this.payloadType = payloadType;
        this.key = key;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public String toString() {
        return "Message{" + "created=" + created + ", id='" + id + '\'' + ", payloadType='" + payloadType + '\''
                + ", key='" + key + '\'' + ", value=" + value + '}';
    }
}
