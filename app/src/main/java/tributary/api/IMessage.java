package tributary.api;

/**
 * Implementation of the IMessage interface, representing a message with a payload of type T.
 * @param <T> The type of the value the message carries.
 */
public interface IMessage<T> {
    /**
    * Retrieves the value contained within the message.
    *
    * @return the value of the message
    */
    public T getValue();

    /**
    * Gets the id of the message.
    *
    * @return the id of the message
    */
    public String getId();

    /**
    * Gets the key associated with the message, typically used for routing or partitioning.
    *
    * @return the key of the message
    */
    public String getKey();

    /**
    * Provides a string representation of the message, usually for logging or debugging purposes.
    *
    * @return a string representation of the message
    */
    public String toString();
}
