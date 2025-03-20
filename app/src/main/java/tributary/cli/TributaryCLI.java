package tributary.cli;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;

import tributary.api.ITopic;
import tributary.api.IConsumerGroup;
import tributary.api.IMessage;
import tributary.api.IPartition;
import tributary.api.IProducer;
import tributary.core.Partition;
import tributary.core.Topic;
import tributary.core.TributaryCluster;
import tributary.core.Rebalancing.RangeRebalanceStrategy;
import tributary.core.Rebalancing.RoundRobinRebalanceStrategy;
import tributary.core.Consumer;
import tributary.core.ConsumerGroup;
import tributary.core.ConsumerGroupManager;
import tributary.core.Message;
import tributary.core.Producer;
import tributary.core.ProducerManager;

public class TributaryCLI {
    private Scanner scanner;

    public TributaryCLI() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Tributary CLI!");
        String command;

        while (true) {
            System.out.print("> ");
            command = scanner.nextLine();
            if ("exit".equalsIgnoreCase(command)) {
                break;
            }
            executeCommand(command);
        }
    }

    public void executeCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            System.out.println("Invalid command");
            return;
        }

        String[] parts = command.split(" ");
        String cmd = parts[0];

        if (parts.length < 2) {
            System.out.println("Invalid command");
            return;
        }

        switch (cmd) {
        case "create":
            handleCreate(parts);
            break;
        case "delete":
            handleDeleteConsumer(parts);
            break;
        case "produce":
            if (parts.length != 5) {
                System.out.println("Invalid input length!");
                return;
            }

            handleProduceEvent(parts[2], parts[3], parts[4]);
            break;
        case "consume":
            handleConsumeEvents(parts);
            break;
        case "show":
            handleShow(parts);
            break;
        case "parallel":
            handleParallel(parts);
            break;
        default:
            System.out.println("Unknown command: " + command);
        }
    }

    private void handleCreate(String[] parts) {
        switch (parts[1]) {
        case "topic":
            handleCreateTopic(parts);
            break;
        case "partition":
            handleCreatePartition(parts);
            break;
        case "consumer":
            if (parts.length == 6) {
                handleCreateConsumerGroup(parts);
            } else if (parts.length == 4) {
                handleCreateConsumer(parts);
            } else {
                System.out.println("Invalid input!");
            }
            break;
        case "producer":
            handleCreateProducer(parts);
            break;
        default:
            System.out.println("Unknown create command");
        }
    }

    private void handleCreateTopic(String[] parts) {
        TributaryCluster cluster = TributaryCluster.getInstance();

        if (parts.length != 4) {
            System.out.println("Invalid input length!");
            return;
        }

        if (cluster.findTopicById(parts[2]) != null) {
            System.out.println("Error: Topic with ID '" + parts[2] + "' already exists.");
            return;
        }

        if (parts[3].equals("String")) {
            Topic<String> stringTopic = new Topic<String>(parts[2], parts[3]);
            cluster.addTopic(stringTopic);
            System.out.println("Created new String topic with ID: " + parts[2]);
        } else if (parts[3].equals("Integer")) {
            Topic<Integer> intTopic = new Topic<Integer>(parts[2], parts[3]);
            cluster.addTopic(intTopic);
            System.out.println("Created new Integer topic with ID: " + parts[2]);
        } else {
            System.out.println("Unknown topic type");
        }

    }

    private void handleCreatePartition(String[] parts) {
        TributaryCluster cluster = TributaryCluster.getInstance();

        if (parts.length != 4) {
            System.out.println("Invalid input length!");
            return;
        }

        ITopic<?> topic = cluster.findTopicById(parts[2]);

        if (topic == null) {
            System.out.println("Error: Topic with ID '" + parts[2] + "' does not exist.");
            return;
        }

        if (cluster.findPartitionById(parts[3]) != null) {
            System.out.println("Error: Partition with ID '" + parts[3] + "' already exists.");
            return;
        }

        if (topic.getType().equals("String")) {
            Partition<String> stringPartition = new Partition<String>(parts[3]);
            topic.addPartition(stringPartition);
            System.out.println("Created new partition with ID: " + parts[3]);
        } else if (topic.getType().equals("Integer")) {
            Partition<Integer> integerPartition = new Partition<Integer>(parts[3]);
            topic.addPartition(integerPartition);
            System.out.println("Created new partition with ID: " + parts[3]);
        }
    }

    private void handleCreateConsumerGroup(String[] parts) {
        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();
        TributaryCluster cluster = TributaryCluster.getInstance();

        if (parts.length != 6) {
            System.out.println("Invalid input length!");
            return;
        }

        ITopic<?> topic = cluster.findTopicById(parts[4]);

        if (topic == null) {
            System.out.println("Error: Topic with ID '" + parts[4] + "' does not exist.");
            return;
        }

        if (groupManager.findConsumerGroupById(parts[3]) != null) {
            System.out.println("Error: Consumer Group with ID '" + parts[3] + "' already exists.");
            return;
        }

        if (parts[5].equals("Range")) {
            ConsumerGroup consumerGroup = new ConsumerGroup(parts[3], parts[4], new RangeRebalanceStrategy());
            groupManager.addConsumerGroup(consumerGroup);
            topic.registerObserver(consumerGroup);
            System.out.println("Consumer group created with ID '" + parts[3] + "' and Range Strategy");
        } else if (parts[5].equals("RoundRobin")) {
            ConsumerGroup consumerGroup = new ConsumerGroup(parts[3], parts[4], new RoundRobinRebalanceStrategy());
            groupManager.addConsumerGroup(consumerGroup);
            topic.registerObserver(consumerGroup);
            System.out.println("Consumer group created with ID '" + parts[3] + "' and Round Robin Strategy");
        } else {
            System.out.println("Invalid Rebalancing Strategy Inputted!");
        }

    }

    private void handleCreateConsumer(String[] parts) {
        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();

        if (parts.length != 4) {
            System.out.println("Invalid input length!");
            return;
        }

        IConsumerGroup consumerGroup = groupManager.findConsumerGroupById(parts[2]);

        if (consumerGroup == null) {
            System.out.println("Error: Consumer Group with ID '" + parts[2] + "' does not exist.");
            return;
        }

        if (groupManager.findConsumerGroupByConsumerId(parts[3]) != null) {
            System.out.println("Error: Consumer with ID '" + parts[3] + "' already exists.");
            return;
        }

        Consumer consumer = new Consumer(parts[3]);
        consumerGroup.addConsumer(consumer);

        if (consumerGroup.hasOnlyOneConsumer()) {
            TributaryCluster cluster = TributaryCluster.getInstance();
            String topicId = consumerGroup.getTopicId();
            ITopic<?> topic = cluster.findTopicById(topicId);
            List<IPartition<?>> partitions = topic.getPartitions();

            for (IPartition<?> partition : partitions) {
                consumer.addPartitionId(partition.getId());
            }
        }
        System.out.println("Consumer created with ID '" + parts[3] + "'");
    }

    private void handleCreateProducer(String[] parts) {
        ProducerManager producerManager = ProducerManager.getInstance();

        if (parts.length != 5) {
            System.out.println("Invalid input length!");
            return;
        }

        if (producerManager.findProducerById(parts[2]) != null) {
            System.out.println("Error: Producer with ID '" + parts[2] + "' already exists.");
            return;
        }

        if (!parts[3].equals("String") && !parts[3].equals("Integer")) {
            System.out.println("Error: '" + parts[3] + "' is not a valid type!");
            return;
        }

        if (parts[4].equals("Random")) {
            Producer producer = new Producer(parts[2], parts[3], parts[4]);
            producerManager.addProducer(producer);
            System.out.println(
                    "Producer created with ID '" + parts[2] + "', type is " + parts[3] + " and Random allocation");
        } else if (parts[4].equals("Manual")) {
            Producer producer = new Producer(parts[2], parts[3], parts[4]);
            producerManager.addProducer(producer);
            System.out.println(
                    "Producer created with ID '" + parts[2] + "', type is " + parts[3] + " and Manual allocation");
        } else {
            System.out.println("Invalid allocation method!");
        }

    }

    private void handleDeleteConsumer(String[] parts) {
        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();

        if (parts.length != 3) {
            System.out.println("Invalid input length!");
            return;
        }

        IConsumerGroup consumerGroup = groupManager.findConsumerGroupByConsumerId(parts[2]);

        if (consumerGroup == null) {
            System.out.println("Error: Consumer with ID '" + parts[2] + "' does not exist.");
            return;
        }

        consumerGroup.removeConsumerById(parts[2]);
        System.out.println("Consumer deleted with ID '" + parts[2] + "'");
        consumerGroup.printConsumerIds();
    }

    private void handleProduceEvent(String producerId, String topicId, String eventId) {
        synchronized (this) {
            TributaryCluster cluster = TributaryCluster.getInstance();
            ProducerManager producerManager = ProducerManager.getInstance();

            ITopic<?> topic = cluster.findTopicById(topicId);

            if (topic == null) {
                System.out.println("Error: Topic with ID '" + topicId + "' does not exist!.");
                return;
            }

            IProducer producer = producerManager.findProducerById(producerId);

            if (producer == null) {
                System.out.println("Error: producer with ID '" + producerId + "' does not exist!.");
                return;
            }

            try {
                String jsonData = new String(Files.readAllBytes(Paths.get(eventId)));
                JSONObject jsonObject = new JSONObject(jsonData);

                String id = jsonObject.getString("id");
                String payloadType = jsonObject.getString("payloadType");
                String key = jsonObject.getString("key");
                String value = jsonObject.getString("value");

                if (!payloadType.equals(topic.getType())) {
                    System.out.println("Message and topic are not compatible types!");
                    return;
                }

                if (cluster.containsMessageWithId(id)) {
                    System.out.println("This event has already been added!");
                    return;
                }

                IMessage<?> message = null;

                if (payloadType.equals("String")) {
                    message = new Message<>(id, payloadType, key, value);
                } else if (payloadType.equals("Integer")) {
                    Integer valueInt = Integer.parseInt(value);
                    message = new Message<>(id, payloadType, key, valueInt);
                } else {
                    System.out.println("Invalid Payload Type!");
                    return;
                }

                if (producer.getAllocation().equals("Random")) {
                    IPartition<?> partition = topic.getRandomPartition();
                    if (partition != null) {
                        partition.enqueueMessage(message);
                        System.out.println("Event (Random) produced successfully: Event ID '" + id + "' in Partition '"
                                + partition.getId() + "'");
                    } else {
                        System.out.println("No partition available!");
                    }
                } else if (producer.getAllocation().equals("Manual")) {
                    IPartition<?> partition = topic.findPartitionById(key);
                    if (partition != null) {
                        partition.enqueueMessage(message);
                        System.out.println("Event (Manual) produced successfully: Event ID '" + id + "' in Partition '"
                                + partition.getId() + "'");
                    } else {
                        System.out.println("No partition available!");
                    }
                } else {
                    System.out.println("Producer does not have valid type!");
                }

            } catch (Exception e) {
                System.out.println("Failed to read or parse JSON data: " + e.getMessage());
            }
        }
    }

    private void handleConsumeEvents(String[] parts) {
        if (parts.length == 4) {
            handleConsumeEvent(parts[2], parts[3]);
            return;
        }

        if (parts.length != 5) {
            System.out.println("Invalid input length!");
            return;
        }

        int numberOfEvents;
        try {
            numberOfEvents = Integer.parseInt(parts[4]);
            if (numberOfEvents < 1) {
                System.out.println("Number of events must be at least 1.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of events: " + parts[4]);
            return;
        }

        for (int i = 0; i < numberOfEvents; i++) {
            handleConsumeEvent(parts[2], parts[3]);
        }
    }

    private void handleConsumeEvent(String consumerId, String partitionId) {
        TributaryCluster cluster = TributaryCluster.getInstance();
        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();

        IPartition<?> partition = cluster.findPartitionById(partitionId);

        if (partition == null) {
            System.out.println("Invalid partition ID!");
        }

        IConsumerGroup consumerGroup = groupManager.findConsumerGroupByConsumerId(consumerId);

        if (consumerGroup == null) {
            System.out.println("Invalid consumer ID!");
        }

        if (partition.hasMessages()) {
            IMessage<?> message = partition.dequeueMessage();
            System.out.println("Event consumed by Consumer ID '" + consumerId + "' and event ID: '" + message.getId()
                    + "', Contents: '" + message.getValue() + "'");
        } else {
            System.out.println("No message available to consume.");
        }
    }

    private void handleShow(String[] parts) {
        switch (parts[1]) {
        case "topic":
            handleShowTopic(parts);
            break;
        case "consumer":
            handleShowConsumerGroup(parts);
            break;
        default:
            System.out.println("Unknown show command");
        }
    }

    private void handleShowTopic(String[] parts) {
        TributaryCluster cluster = TributaryCluster.getInstance();

        if (parts.length != 3) {
            System.out.println("Invalid input length!");
            return;
        }

        ITopic<?> topic = cluster.findTopicById(parts[2]);

        if (topic == null) {
            System.out.println("Error: Topic with ID '" + parts[2] + "' does not exist.");
            return;
        }

        topic.displayTopicDetails();

    }

    private void handleShowConsumerGroup(String[] parts) {
        ConsumerGroupManager groupManager = ConsumerGroupManager.getInstance();

        if (parts.length != 4) {
            System.out.println("Invalid input length!");
            return;
        }

        IConsumerGroup consumerGroup = groupManager.findConsumerGroupById(parts[3]);

        if (consumerGroup == null) {
            System.out.println("Error: Consumer Group with ID '" + parts[3] + "' does not exist.");
            return;
        }

        consumerGroup.printConsumersAndPartitions();
    }

    private void handleParallel(String[] parts) {
        switch (parts[1]) {
        case "produce":
            handleParallelProduce(parts);
            break;
        case "consume":
            handleParallelConsume(parts);
            break;
        default:
            System.out.println("Unknown show command");
        }
    }

    private void handleParallelProduce(String[] parts) {
        if ((parts.length - 2) % 3 != 0 || parts.length < 5) {
            System.out.println("Invalid input length!");
            return;
        }

        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 2; i < parts.length; i += 3) {
            final String producerId = parts[i].trim();
            final String topicId = parts[i + 1].trim();
            final String eventId = parts[i + 2].trim();

            Thread thread = new Thread(() -> handleProduceEvent(producerId, topicId, eventId));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }
    }

    private void handleParallelConsume(String[] parts) {
        if ((parts.length - 2) % 2 != 0 || parts.length < 4) {
            System.out.println("Invalid input length!");
            return;
        }

        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 2; i < parts.length; i += 2) {
            final String consumerId = parts[i].trim();
            final String partitionId = parts[i + 1].trim();

            Thread thread = new Thread(() -> handleConsumeEvent(consumerId, partitionId));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        TributaryCLI cli = new TributaryCLI();
        cli.start();
    }
}
