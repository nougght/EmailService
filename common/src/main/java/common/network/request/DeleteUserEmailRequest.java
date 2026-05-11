package common.network.request;

import common.dto.Draft;

import java.util.UUID;

public class DeleteUserEmailRequest extends Request{
    private UUID userId;
    private UUID emailId;

    public DeleteUserEmailRequest() {super();}
    public DeleteUserEmailRequest(UUID userId, UUID emailId) {
        super("DeleteUserEmail");
        this.userId = userId;
        this.emailId = emailId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getEmailId() {
        return emailId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setEmailId(UUID emailId) {
        this.emailId = emailId;
    }
}
