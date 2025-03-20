package tributary.api;

/**
 * Interface defining the operations for a partition in a messaging system.
 * @param <T> The type of messages this partition holds.
 */
public interface IPartition<T> {
    /**
    * Gets the id of the partition.
    *
    * @return the id of the partition
    */
    String getId();

    /**
     * Prints all messages currently in the partition
     */
    void printMessages();

    /**
    * Enqueues a message into this partition.
    *
    * @param message the message to be added to the partition
    */
    void enqueueMessage(IMessage<?> message);

    /**
    * Dequeues and returns the oldest message from the partition.
    *
    * @return the oldest message, or null if the partition is empty
    */
    IMessage<?> dequeueMessage();

    /**
    * Checks if the partition contains any messages.
    *
    * @return true if there are messages, false otherwise
    */
    boolean hasMessages();

    /**
    * Checks if the partition contains a message with a specific id.
    *
    * @param id the id of the message to find
    * @return true if a message with the specified id exists, false otherwise
    */
    boolean containsMessageId(String id);
}
