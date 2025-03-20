package tributary.core;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;

import tributary.api.IConsumerGroup;
import tributary.api.IConsumer;
import tributary.core.Rebalancing.*;

public class ConsumerGroup implements IConsumerGroup {
    private List<IConsumer> consumers;
    private String id;
    private String topicId;
    private RebalancingStrategy rebalancingStrategy;
    private Random random = new Random();

    public ConsumerGroup(String id, String topicId, RebalancingStrategy rebalancingStrategy) {
        this.id = id;
        this.topicId = topicId;
        this.rebalancingStrategy = rebalancingStrategy;
        consumers = new ArrayList<IConsumer>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTopicId() {
        return topicId;
    }

    @Override
    public void addConsumer(IConsumer consumer) {
        consumers.add(consumer);
    }

    @Override
    public IConsumer findConsumerById(String id) {
        return consumers.stream().filter(consumer -> consumer.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void removeConsumerById(String consumerId) {
        Iterator<IConsumer> iterator = consumers.iterator();
        while (iterator.hasNext()) {
            IConsumer consumer = iterator.next();
            if (consumer.getId().equals(consumerId)) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void printConsumerIds() {
        System.out.println("Consumer IDs in group '" + id + "':");
        for (IConsumer consumer : consumers) {
            System.out.println(consumer.getId());
        }
    }

    @Override
    public void allocatePartition(String partitionId) {
        if (consumers.isEmpty()) {
            System.out.println("No consumers available to allocate partition.");
            return;
        }
        int randomIndex = random.nextInt(consumers.size());
        IConsumer selectedConsumer = consumers.get(randomIndex);
        selectedConsumer.addPartitionId(partitionId);
    }

    @Override
    public void printConsumersAndPartitions() {
        System.out.println("Consumer Group '" + id + "' Details:");
        if (consumers.isEmpty()) {
            System.out.println("No consumers in this group.");
        } else {
            for (IConsumer consumer : consumers) {
                System.out.print("Consumer ID: " + consumer.getId() + " - consuming from partition IDs: ");
                List<String> partitions = consumer.getPartitionIds();
                if (partitions.isEmpty()) {
                    System.out.println("No partitions assigned.");
                } else {
                    System.out.println(partitions);
                }
            }
        }
    }

    @Override
    public boolean hasOnlyOneConsumer() {
        return consumers.size() == 1;
    }
}
