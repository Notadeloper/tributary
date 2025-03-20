package tributary.core;

import tributary.api.IProducer;

public class Producer implements IProducer {
    private String id;
    private String type;
    private String allocation;

    public Producer(String id, String type, String allocation) {
        this.id = id;
        this.type = type;
        this.allocation = allocation;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getAllocation() {
        return allocation;
    }
}
