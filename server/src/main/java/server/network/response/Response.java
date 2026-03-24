package server.network.response;

import java.util.UUID;

// to do - обернуть полезные данные в отдельный объект
public abstract class Response {
    protected UUID requestId;
    protected String type;
    protected String status;

    public Response() {
    }

    public Response(UUID requestId, String type, String status) {
        this.requestId = requestId;
        this.type = type;
        this.status = status;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getType() {
        return type;
    }
}