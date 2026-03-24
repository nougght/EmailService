package client.network.request;

import java.util.UUID;


public class GetEmailsRequest extends Request {
    private UUID userId;

    GetEmailsRequest() {
        super();
    }

    public GetEmailsRequest(UUID userId) {
        super("GetEmails");
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
