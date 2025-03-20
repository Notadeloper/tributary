package tributary.core;

import tributary.api.ITributaryCluster;
import tributary.api.IPartition;
import tributary.api.ITopic;
import java.util.List;
import java.util.ArrayList;

public class TributaryCluster implements ITributaryCluster {
    private static TributaryCluster instance;
    private List<ITopic<?>> topics;

    private TributaryCluster() {
        topics = new ArrayList<>();
    }

    public static void resetInstance() {
        instance = null;
    }

    public static synchronized TributaryCluster getInstance() {
        if (instance == null) {
            instance = new TributaryCluster();
        }
        return instance;
    }

    @Override
    public void addTopic(ITopic<?> topic) {
        topics.add(topic);
    }

    @Override
    public List<ITopic<?>> getTopics() {
        return new ArrayList<>(topics);
    }

    @Override
    public ITopic<?> findTopicById(String id) {
        return topics.stream().filter(topic -> topic.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public IPartition<?> findPartitionById(String partitionId) {
        for (ITopic<?> topic : topics) {
            IPartition<?> partition = topic.findPartitionById(partitionId);
            if (partition != null) {
                return partition;
            }
        }
        return null;
    }

    @Override
    public boolean containsMessageWithId(String messageId) {
        for (ITopic<?> topic : topics) {
            if (topic.containsMessageWithId(messageId)) {
                return true;
            }
        }
        return false;
    }
}
