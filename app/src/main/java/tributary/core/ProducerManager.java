package tributary.core;

import java.util.ArrayList;
import java.util.List;

import tributary.api.IProducer;
import tributary.api.IProducerManager;

public class ProducerManager implements IProducerManager {
    private static ProducerManager instance;
    private List<IProducer> producers;

    private ProducerManager() {
        producers = new ArrayList<>();
    }

    public static synchronized ProducerManager getInstance() {
        if (instance == null) {
            instance = new ProducerManager();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    @Override
    public void addProducer(IProducer producer) {
        producers.add(producer);
    }

    @Override
    public IProducer findProducerById(String id) {
        return producers.stream().filter(producer -> producer.getId().equals(id)).findFirst().orElse(null);
    }
}
