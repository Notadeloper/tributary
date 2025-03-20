package tributary.api;

import java.util.List;

/**
 * Interface defining the structure and operations for a topic within the messaging system.
 * @param <T> The type of messages the topic holds.
 */
public interface ITopic<T> {
    /**
    * Registers an observer (consumer group) to be notified of changes within the topic.
    *
    * @param observer the consumer group to be notified
    */
    void registerObserver(IConsumerGroup observer);

    /**
    * Retrieves the id of the topic.
    *
    * @return the topic's id
    */
    String getId();

    /**
    * Retrieves the type of messages stored in this topic.
    *
    * @return the message type
    */
    String getType();

    /**
    * Notifies registered observers about a new partition within the topic.
    *
    * @param partition the partition that was added
    */
    void notifyObservers(IPartition<?> partition);

    /**
    * Adds a new partition to the topic and notifies observers.
    *
    * @param partition the partition to add
    */
    void addPartition(IPartition<?> partition);

    /**
    * Retrieves all partitions within the topic.
    *
    * @return a list of partitions
    */
    List<IPartition<?>> getPartitions();

    /**
    * Finds a partition by its id.
    *
    * @param id the id of the partition
    * @return the partition if found, otherwise null
    */
    IPartition<?> findPartitionById(String id);

    /**
    * Displays details of the topic including its id, type, and all messages within each partition.
    */
    void displayTopicDetails();

    /**
    * Retrieves a random partition from the topic, useful for load balancing or random allocation.
    *
    * @return a randomly selected partition, or null if no partitions exist
    */
    IPartition<?> getRandomPartition();

    /**
     * Checks if any partition within the topic contains a message with the specified id.
     *
     * @param messageId the id of the message to find
     * @return true if the message is found, otherwise false
     */
    boolean containsMessageWithId(String messageId);
}
