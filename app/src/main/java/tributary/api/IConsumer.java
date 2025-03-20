package tributary.api;

import java.util.List;

/**
 * Implementation of the IConsumer interface
 */
public interface IConsumer {
    /**
    * Retrieves the unique identifier for this consumer.
    *
    * @return the unique ID of the consumer
    */
    String getId();

    /**
     * Adds a partition ID to the list of partitions that this consumer is consuming from.
     *
     * @param id the partition ID to be added to the consumer's list
     */
    void addPartitionId(String id);

    /**
     * Retrieves a list of partition IDs that this consumer is consuming from.
     *
     * @return a list of partition IDs
     */
    List<String> getPartitionIds();
}
