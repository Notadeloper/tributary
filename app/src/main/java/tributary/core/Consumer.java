package tributary.core;

import java.util.List;
import java.util.ArrayList;

import tributary.api.IConsumer;

public class Consumer implements IConsumer {
    private String id;
    private List<String> partitionIds;

    public Consumer(String id) {
        this.id = id;
        this.partitionIds = new ArrayList<String>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void addPartitionId(String id) {
        partitionIds.add(id);
    }

    @Override
    public List<String> getPartitionIds() {
        return new ArrayList<>(partitionIds);
    }
}
