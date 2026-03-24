package server.network.response;

import server.dto.EmailDTO;

import java.util.ArrayList;
import java.util.UUID;

public class GetEmailsResponse extends Response{
    private UUID userId;
    private ArrayList<EmailDTO> emails;

    public GetEmailsResponse(){super();}
    public GetEmailsResponse(UUID requestId, String status, UUID userId, ArrayList<EmailDTO> emails)
    {
        super(requestId, "GetEmails", status);
        this.emails = emails;
    }

    public UUID getUserId() {
        return userId;
    }

    public ArrayList<EmailDTO> getEmails() {
        return emails;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setEmails(ArrayList<EmailDTO> emails) {
        this.emails = emails;
    }
}
