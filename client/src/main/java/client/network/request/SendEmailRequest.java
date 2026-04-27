package client.network.request;

import java.util.List;
import java.util.UUID;

public class SendEmailRequest extends Request {
    private UUID senderId;
    private List<String> recipientUsernames;

    private String subject;
    private String body;



    public SendEmailRequest() {
        super();
    }

    public SendEmailRequest(UUID senderId, List<String> recipientUsernames, String subject, String body) {
        super("SendEmail");
        this.senderId = senderId;
        this.recipientUsernames = recipientUsernames;
        this.subject = subject;
        this.body = body;

    }

    public UUID getSenderId() {
        return senderId;
    }

    public List<String> getRecipientUsernames() {
        return recipientUsernames;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public void setRecipientUsernames(List<String> recipientUsernames) {
        this.recipientUsernames = recipientUsernames;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }
}