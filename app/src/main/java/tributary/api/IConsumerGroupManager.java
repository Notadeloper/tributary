package tributary.api;

/**
 * Interface for managing consumer groups within the tributary system.
 * This interface provides methods to manage and retrieve consumer groups based on identifiers.
 */
public interface IConsumerGroupManager {
    /**
    * Adds a consumer group to the manager.
    *
    * @param consumerGroup the consumer group to be added
    */
    void addConsumerGroup(IConsumerGroup consumerGroup);

    /**
    * Finds and returns a consumer group by its id.
    *
    * @param id the id of the consumer group
    * @return the consumer group if found, otherwise null
    */
    IConsumerGroup findConsumerGroupById(String id);

    /**
    * Finds and returns a consumer group based on a consumer's id.
    * This method searches through all consumer groups to find a group that contains a specific consumer.
    *
    * @param consumerId the id of the consumer
    * @return the consumer group if found, otherwise null
    */
    IConsumerGroup findConsumerGroupByConsumerId(String consumerId);
}
