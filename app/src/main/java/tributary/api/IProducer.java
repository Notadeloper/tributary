package tributary.api;

/**
 * Interface defining the basic properties and functions of a producer.
 */
public interface IProducer {
    /**
    * Gets the id of the producer.
    *
    * @return the id of the producer
    */
    String getId();

    /**
    * Gets the type of messages this producer generates.
    *
    * @return the type of the messages
    */
    String getType();

    /**
    * Gets the allocation strategy used by the producer to determine which partition to send messages.
    *
    * @return the allocation strategy of the producer
    */
    String getAllocation();
}
