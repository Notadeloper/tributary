package tributary.api;

/**
 * Interface for managing a group of consumers
 * This interface provides methods to manage consumers and partitions they consume from.
 */
public interface IConsumerGroup {
    /**
    * Gets the unique idfor this consumer group.
    *
    * @return the id of this consumer group
    */
    String getId();

    /**
     * Gets the id of the topic that this group is consuming from.
     *
     * @return the topic id
     */
    String getTopicId();

    /**
    * Adds a consumer to the consumer group.
    *
    * @param consumer the consumer to be added
    */
    void addConsumer(IConsumer consumer);

    /**
    * Finds a consumer by their id within the group.
    *
    * @param id the id of the consumer
    * @return the consumer if found, otherwise null
    */
    IConsumer findConsumerById(String id);

    /**
    * Removes a consumer from the consumer group by their id.
    *
    * @param consumerId the id of the consumer to remove
    */
    void removeConsumerById(String consumerId);

    /**
    * Prints the ids of all consumers within the group
    */
    void printConsumerIds();

    /**
    * Allocates a partition to a random consumer in the group.
    *
    * @param partitionId the id of the partition to allocate
    */
    void allocatePartition(String partitionId);

    /**
    * Prints details of all consumers and their assigned partitions
    */
    void printConsumersAndPartitions();

    /**
    * Checks if the consumer group contains only one consumer.
    *
    * @return true if there is only one consumer, false otherwise
    */
    boolean hasOnlyOneConsumer();
}
