package tributary.api;

/**
 * Interface for managing producers.
 * This interface provides methods to add and retrieve producers by their id.
 */
public interface IProducerManager {
    /**
    * Adds a producer to the management system.
    *
    * @param producer the producer to be added
    */
    public void addProducer(IProducer producer);

    /**
     * Finds a producer by its id.
     *
     * @param id the id of the producer to find
     * @return the producer if found, otherwise null
     */
    public IProducer findProducerById(String id);

}
