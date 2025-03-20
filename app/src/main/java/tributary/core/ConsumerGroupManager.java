package tributary.core;

import java.util.ArrayList;
import java.util.List;

import tributary.api.IConsumerGroup;
import tributary.api.IConsumerGroupManager;

public class ConsumerGroupManager implements IConsumerGroupManager {
    private static ConsumerGroupManager instance;
    private List<IConsumerGroup> consumerGroups;

    private ConsumerGroupManager() {
        consumerGroups = new ArrayList<IConsumerGroup>();
    }

    public static void resetInstance() {
        instance = null;
    }

    public static synchronized ConsumerGroupManager getInstance() {
        if (instance == null) {
            instance = new ConsumerGroupManager();
        }
        return instance;
    }

    @Override
    public void addConsumerGroup(IConsumerGroup consumerGroup) {
        consumerGroups.add(consumerGroup);
    }

    @Override
    public IConsumerGroup findConsumerGroupById(String id) {
        return consumerGroups.stream().filter(consumerGroup -> consumerGroup.getId().equals(id)).findFirst()
                .orElse(null);
    }

    @Override
    public IConsumerGroup findConsumerGroupByConsumerId(String consumerId) {
        return consumerGroups.stream().filter(consumerGroup -> consumerGroup.findConsumerById(consumerId) != null)
                .findFirst().orElse(null);
    }
}
