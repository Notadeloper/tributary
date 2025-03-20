package tributary.core;

import tributary.api.ITopic;
import tributary.api.IConsumerGroup;
import tributary.api.IPartition;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Topic<T> implements ITopic<T> {
    private String id;
    private String type;
    private List<IPartition<?>> partitions;
    private List<IConsumerGroup> observers;
    private Random random = new Random();

    public Topic(String id, String type) {
        this.id = id;
        this.type = type;
        partitions = new ArrayList<IPartition<?>>();
        observers = new ArrayList<IConsumerGroup>();
    }

    @Override
    public void registerObserver(IConsumerGroup observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(IPartition<?> partition) {
        for (IConsumerGroup observer : observers) {
            observer.allocatePartition(partition.getId());
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void addPartition(IPartition<?> partition) {
        partitions.add(partition);
        notifyObservers(partition);
    }

    @Override
    public List<IPartition<?>> getPartitions() {
        return new ArrayList<>(partitions);
    }

    @Override
    public IPartition<?> findPartitionById(String id) {
        return partitions.stream().filter(partition -> partition.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void displayTopicDetails() {
        System.out.println("Topic ID: " + getId() + " | Type: " + getType());
        if (partitions.isEmpty()) {
            System.out.println("No partitions available.");
        } else {
            for (IPartition<?> partition : partitions) {
                partition.printMessages();
            }
        }
    }

    @Override
    public IPartition<?> getRandomPartition() {
        if (partitions.isEmpty()) {
            return null;
        }
        int index = random.nextInt(partitions.size());
        return partitions.get(index);
    }

    @Override
    public boolean containsMessageWithId(String messageId) {
        for (IPartition<?> partition : partitions) {
            if (partition.containsMessageId(messageId)) {
                return true;
            }
        }
        return false;
    }
}
