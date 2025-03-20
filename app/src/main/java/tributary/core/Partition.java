package tributary.core;

import tributary.api.IPartition;
import tributary.api.IMessage;
import java.util.LinkedList;
import java.util.Queue;

public class Partition<T> implements IPartition<T> {
    private String id;
    private Queue<IMessage<?>> messages;

    public Partition(String id) {
        this.id = id;
        this.messages = new LinkedList<>();
    }

    @Override
    public void enqueueMessage(IMessage<?> message) {
        messages.add(message);
    }

    @Override
    public IMessage<?> dequeueMessage() {
        return messages.poll();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void printMessages() {
        if (messages.isEmpty()) {
            System.out.println("No messages in partition " + id);
        } else {
            System.out.println("Messages in partition " + id + ":");
            for (IMessage<?> message : messages) {
                System.out.println(message.toString());
            }
        }
    }

    @Override
    public boolean hasMessages() {
        return !messages.isEmpty();
    }

    @Override
    public boolean containsMessageId(String id) {
        for (IMessage<?> message : messages) {
            if (message.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
