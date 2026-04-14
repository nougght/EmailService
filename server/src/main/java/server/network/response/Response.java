package server.network.response;

import server.network.message.Message;

import java.util.UUID;

// ответ сервера на запрос клиента
public abstract class Response extends Message {
    protected UUID requestId;
    protected String status;

    public Response() {
        super();
    }

    public Response(UUID requestId, String type, String status) {
        super("RESPONSE", type);
        this.requestId = requestId;
        this.status = status;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }
}