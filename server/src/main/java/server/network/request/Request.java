package server.network.request;

import java.util.UUID;



public abstract class Request {
    protected UUID requestId;
    protected String type;


    public Request(){}

    public Request(String type) {
        this.type = type;
        this.requestId = UUID.randomUUID();
    }

    public UUID getRequestId() {
        return requestId;
    }

    public String getType() {
        return type;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public void setType(String type) {
        this.type = type;
    }
}
