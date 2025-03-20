package tributary.api;

import java.util.List;

/**
 * Interface for managing a collection of topics in the tributary
 */
public interface ITributaryCluster {
    /**
    * Adds a topic to the tributary cluster.
    *
    * @param topic the topic to add
    */
    void addTopic(ITopic<?> topic);

    /**
    * Retrieves all topics in the tributary cluster.
    *
    * @return a list of all topics
    */
    List<ITopic<?>> getTopics();

    /**
    * Checks if any topic in the cluster contains a message with the specified id.
    *
    * @param messageId the id of the message to find
    * @return true if a message with the specified id exists in any topic, false otherwise
    */
    boolean containsMessageWithId(String messageId);

    /**
    * Finds a topic by its id.
    *
    * @param id the id of the topic to find
    * @return the topic if found, otherwise null
    */
    ITopic<?> findTopicById(String id);

    /**
    * Finds a partition by its id across all topics.
    *
    * @param partitionId the id of the partition to find
    * @return the partition if found, otherwise null
    */
    IPartition<?> findPartitionById(String partitionId);
}
