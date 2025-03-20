package tributary;

import tributary.core.*;
import tributary.core.Rebalancing.RangeRebalanceStrategy;
import tributary.core.Rebalancing.RoundRobinRebalanceStrategy;
import tributary.cli.*;
import tributary.api.*;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class TributaryTest {
    @BeforeEach
    public void setUp() {
        TributaryCluster.resetInstance();
        ConsumerGroupManager.resetInstance();
        ProducerManager.resetInstance();
    }

    @Test
    public void topicAndPartitionComponentTest() {
        TributaryCluster cluster = TributaryCluster.getInstance();
        Topic<?> topicStr = new Topic<>("Topic1", "String");
        Topic<?> topicInt = new Topic<>("Topic2", "Integer");

        cluster.addTopic(topicStr);
        cluster.addTopic(topicInt);

        assertNull(topicStr.getRandomPartition());

        assertNotNull(cluster.findTopicById("Topic1"));
        assertNotNull(cluster.findTopicById("Topic2"));
        assertNull(cluster.findTopicById("Topic3"));

        Partition<?> partitionStr1 = new Partition<>("PartitionStr1");
        Partition<?> partitionInt1 = new Partition<>("PartitionInt1");
        Partition<?> partitionStr2 = new Partition<>("PartitionStr2");
        Partition<?> partitionInt2 = new Partition<>("PartitionInt2");

        topicStr.addPartition(partitionStr2);
        topicStr.addPartition(partitionStr1);
        topicInt.addPartition(partitionInt1);
        topicInt.addPartition(partitionInt2);

        topicStr.displayTopicDetails();

        assertNotNull(cluster.findPartitionById("PartitionStr1"));
        assertNull(cluster.findPartitionById("PartitionStr5"));

        assertNotNull(topicStr.findPartitionById("PartitionStr1"));
        assertNotNull(topicStr.findPartitionById("PartitionStr2"));
        assertNotNull(topicInt.findPartitionById("PartitionInt1"));
        assertNotNull(topicInt.findPartitionById("PartitionInt2"));

        assertEquals(2, topicInt.getPartitions().size());

        assertNotNull(topicStr.getRandomPartition());

        assertNull(topicInt.findPartitionById("PartitionInt7"));
    }

    @Test
    public void consumerGroupAndConsumerComponentTest() {
        Topic<?> topicStr = new Topic<>("Topic1", "String");

        Partition<?> partitionStr1 = new Partition<>("PartitionStr1");
        topicStr.addPartition(partitionStr1);

        Partition<?> partitionStr2 = new Partition<>("PartitionStr1");
        topicStr.addPartition(partitionStr2);

        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();

        ConsumerGroup group1 = new ConsumerGroup("ConsumerGroup1", "Topic1", new RangeRebalanceStrategy());
        ConsumerGroup group2 = new ConsumerGroup("ConsumerGroup2", "Topic1", new RoundRobinRebalanceStrategy());

        groupManager.addConsumerGroup(group1);
        groupManager.addConsumerGroup(group2);

        assertNotNull(groupManager.findConsumerGroupById("ConsumerGroup1"));
        assertNotNull(groupManager.findConsumerGroupById("ConsumerGroup2"));

        Consumer consumer1 = new Consumer("Consumer1");
        Consumer consumer2 = new Consumer("Consumer2");

        group1.addConsumer(consumer1);
        group2.addConsumer(consumer2);

        assertNotNull(group1.findConsumerById("Consumer1"));
        assertNotNull(group2.findConsumerById("Consumer2"));

        group1.removeConsumerById("Consumer1");

        assertNull(group1.findConsumerById("Consumer1"));
        assertNull(groupManager.findConsumerGroupByConsumerId("Consumer1"));
        assertNotNull(groupManager.findConsumerGroupByConsumerId("Consumer2"));

        group1.printConsumerIds();
        group2.printConsumerIds();
        group2.printConsumersAndPartitions();

        group2.allocatePartition("PartitionStr1");
        assertEquals(1, consumer2.getPartitionIds().size());

        group1.allocatePartition("PartitionStr2");
        assertEquals(0, consumer1.getPartitionIds().size());

    }

    @Test
    public void producerUnitTest() {
        ProducerManager producerManager = ProducerManager.getInstance();
        Producer producer1 = new Producer("Producer1", "String", "Random");

        assertEquals(producer1.getType(), "String");
        assertEquals(producer1.getId(), "Producer1");

        assertNull(producerManager.findProducerById("Producer1"));
        producerManager.addProducer(producer1);
        assertNotNull(producerManager.findProducerById("Producer1"));
    }

    @Test
    public void messageUnitTest() {
        Producer producer1 = new Producer("Producer1", "String", "Random");

        Message<?> message1 = new Message<>("msg1", "String", "5", "Hello");

        assertEquals(producer1.getType(), "String");
        assertEquals(producer1.getId(), "Producer1");

        assertEquals(message1.getId(), "msg1");
        assertEquals(message1.getKey(), "5");
        assertEquals(message1.getValue(), "Hello");

        TributaryCluster cluster = TributaryCluster.getInstance();
        Topic<?> topicStr = new Topic<>("Topic1", "String");
        Topic<?> topicInt = new Topic<>("Topic2", "Integer");

        cluster.addTopic(topicStr);
        cluster.addTopic(topicInt);

        assertNotNull(cluster.findTopicById("Topic1"));
        assertNotNull(cluster.findTopicById("Topic2"));

        Partition<?> partitionStr1 = new Partition<>("PartitionStr1");
        Partition<?> partitionInt1 = new Partition<>("PartitionInt1");

        topicStr.addPartition(partitionStr1);
        topicInt.addPartition(partitionInt1);

        assertTrue(!partitionStr1.containsMessageId("msg1"));
        assertTrue(!cluster.containsMessageWithId("msg1"));
        assertTrue(!topicStr.containsMessageWithId("msg1"));

        partitionStr1.enqueueMessage(message1);
        assertTrue(partitionStr1.containsMessageId("msg1"));
        assertTrue(cluster.containsMessageWithId("msg1"));
        assertTrue(topicStr.containsMessageWithId("msg1"));

        partitionStr1.printMessages();
        assertTrue(partitionStr1.hasMessages());
        assertTrue(!partitionInt1.hasMessages());
    }

    @Test
    public void testEventProduceRandomCLI() {
        TributaryCLI cli = new TributaryCLI();

        cli.executeCommand("Invalid Command");

        cli.executeCommand("create topic 5 String");
        cli.executeCommand("create partition 5 10");
        cli.executeCommand("create producer 5 String Random");
        cli.executeCommand("produce event 5 5 src/test/java/tributary/eventJSONs/event1.json");
        cli.executeCommand("produce event 5 5 src/test/java/tributary/eventJSONs/event2.json");
        cli.executeCommand("create consumer group 10 5 Range");
        cli.executeCommand("create consumer 10 20");

        TributaryCluster cluster = TributaryCluster.getInstance();
        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();

        ITopic<?> topic = cluster.findTopicById("5");
        IPartition<?> partition = topic.findPartitionById("10");

        IConsumerGroup group = groupManager.findConsumerGroupById("10");
        IConsumer consumer = group.findConsumerById("20");

        assertTrue(partition.containsMessageId("event1"));
        assertTrue(partition.containsMessageId("event2"));
        assertTrue(!partition.containsMessageId("event3"));

        assertEquals(consumer.getPartitionIds(), Arrays.asList("10"));
    }

    @Test
    public void testEventProduceManualCLI() {
        TributaryCLI cli = new TributaryCLI();

        cli.executeCommand("create topic topic1 Integer");
        cli.executeCommand("create partition topic1 partition1");
        cli.executeCommand("create partition topic1 partition2");
        cli.executeCommand("create consumer group group1 topic1 Range");
        cli.executeCommand("create consumer group1 consumer1");
        cli.executeCommand("create producer producer1 Integer Manual");
        cli.executeCommand("produce event producer1 topic1 src/test/java/tributary/eventJSONs/event5.json");
        cli.executeCommand("produce event producer1 topic1 src/test/java/tributary/eventJSONs/event6.json");

        TributaryCluster cluster = TributaryCluster.getInstance();
        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();

        ITopic<?> topic = cluster.findTopicById("topic1");
        IPartition<?> partition1 = topic.findPartitionById("partition1");
        IPartition<?> partition2 = topic.findPartitionById("partition2");

        IConsumerGroup group = groupManager.findConsumerGroupById("group1");
        IConsumer consumer = group.findConsumerById("consumer1");

        assertTrue(partition1.containsMessageId("event5"));
        assertTrue(partition2.containsMessageId("event6"));

        assertEquals(consumer.getPartitionIds(), Arrays.asList("partition1", "partition2"));
    }

    @Test
    public void testEventConsumeCLI() {
        TributaryCLI cli = new TributaryCLI();

        cli.executeCommand("create topic topic1 String");
        cli.executeCommand("create partition topic1 partition1");
        cli.executeCommand("create consumer group group1 topic1 Range");
        cli.executeCommand("create consumer group1 consumer1");
        cli.executeCommand("create producer producer1 String Random");
        cli.executeCommand("produce event producer1 topic1 src/test/java/tributary/eventJSONs/event1.json");
        cli.executeCommand("produce event producer1 topic1 src/test/java/tributary/eventJSONs/event2.json");
        cli.executeCommand("produce event producer1 topic1 src/test/java/tributary/eventJSONs/event3.json");
        cli.executeCommand("produce event producer1 topic1 src/test/java/tributary/eventJSONs/event4.json");

        TributaryCluster cluster = TributaryCluster.getInstance();
        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();

        ITopic<?> topic = cluster.findTopicById("topic1");
        IPartition<?> partition1 = topic.findPartitionById("partition1");

        IConsumerGroup group = groupManager.findConsumerGroupById("group1");
        IConsumer consumer = group.findConsumerById("consumer1");

        assertTrue(partition1.containsMessageId("event1"));
        assertTrue(partition1.containsMessageId("event2"));
        assertTrue(partition1.containsMessageId("event3"));
        assertTrue(partition1.containsMessageId("event4"));

        assertEquals(consumer.getPartitionIds(), Arrays.asList("partition1"));

        cli.executeCommand("consume event consumer1 partition1");

        assertTrue(!partition1.containsMessageId("event1"));
        assertTrue(partition1.containsMessageId("event2"));
        assertTrue(partition1.containsMessageId("event3"));
        assertTrue(partition1.containsMessageId("event4"));

        cli.executeCommand("consume event consumer1 partition1 3");

        assertTrue(!partition1.containsMessageId("event1"));
        assertTrue(!partition1.containsMessageId("event2"));
        assertTrue(!partition1.containsMessageId("event3"));
        assertTrue(!partition1.containsMessageId("event4"));
    }

    @Test
    public void parallelProduceAndConsumeTest() {
        TributaryCLI cli = new TributaryCLI();

        cli.executeCommand("create topic topic1 String");
        cli.executeCommand("create consumer group group1 topic1 Range");
        cli.executeCommand("create consumer group1 consumer1");
        cli.executeCommand("create consumer group1 consumer2");
        cli.executeCommand("create partition topic1 partition1");
        cli.executeCommand("create partition topic1 partition2");
        cli.executeCommand("create producer producer1 String Manual");
        cli.executeCommand("parallel produce producer1 topic1 src/test/java/tributary/eventJSONs/event1.json "
                + "producer1 topic1 src/test/java/tributary/eventJSONs/event3.json ");
        cli.executeCommand("parallel produce producer1 topic1 src/test/java/tributary/eventJSONs/event2.json "
                + "producer1 topic1 src/test/java/tributary/eventJSONs/event4.json");

        TributaryCluster cluster = TributaryCluster.getInstance();
        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();

        ITopic<?> topic = cluster.findTopicById("topic1");
        IPartition<?> partition1 = topic.findPartitionById("partition1");
        IPartition<?> partition2 = topic.findPartitionById("partition2");

        IConsumerGroup group = groupManager.findConsumerGroupById("group1");
        IConsumer consumer1 = group.findConsumerById("consumer1");
        IConsumer consumer2 = group.findConsumerById("consumer2");

        assertTrue(partition1.containsMessageId("event1"));
        assertTrue(partition1.containsMessageId("event2"));
        assertTrue(partition2.containsMessageId("event3"));
        assertTrue(partition2.containsMessageId("event4"));

        // Because rebalancing was not implemented, this part is non deterministic as consumers and partitions are
        // assigned randomly - this is assuming a particular consumer and partition alignment
        if (consumer1.getPartitionIds().equals(Arrays.asList("partition1"))
                && consumer2.getPartitionIds().equals(Arrays.asList("partition2"))) {
            cli.executeCommand("parallel consume consumer1 partition1 consumer2 partition2");
            assertTrue(!partition1.containsMessageId("event1"));
            assertTrue(partition1.containsMessageId("event2"));
            assertTrue(!partition2.containsMessageId("event3"));
            assertTrue(partition2.containsMessageId("event4"));

            cli.executeCommand("parallel consume consumer1 partition1 consumer2 partition2");
            assertTrue(!partition1.containsMessageId("event1"));
            assertTrue(!partition1.containsMessageId("event2"));
            assertTrue(!partition2.containsMessageId("event3"));
            assertTrue(!partition2.containsMessageId("event4"));
        }
    }
}
