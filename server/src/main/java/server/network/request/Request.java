package server.network.request;

import java.util.UUID;



public abstract class Request {
    protected UUID requestId;
    protected String type;
    protected String accessToken;

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
