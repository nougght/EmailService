package client.network.request;

import client.network.message.Message;

import java.util.UUID;


// запрос от клиента к серверу
public abstract class Request extends Message {
    protected UUID requestId;
    protected String accessToken;

    public Request(){
        super();
    }

    public Request(String type) {
        super("REQUEST", type);
        this.requestId = UUID.randomUUID();
    }

    public UUID getRequestId() {
        return requestId;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }


    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
