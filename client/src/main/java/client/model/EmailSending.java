package client.model;

import java.util.UUID;

public class EmailSending {
    private UUID senderId;
    private String receiverUsername;

    private String subject;
    private String body;

    public EmailSending(UUID senderId, String receiverUsername, String subject, String body) {
        this.senderId = senderId;
        this.receiverUsername = receiverUsername;
        this.subject = subject;
        this.body = body;

    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
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

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
