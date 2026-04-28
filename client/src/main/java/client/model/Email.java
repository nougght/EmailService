package client.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Email {
    private UUID emailId;
    private UUID senderId;
    private List<UUID> recipientIds;

    private String subject;
    private String body;
    private OffsetDateTime sentAt;

    private User sender;
    private List<User> recipients;

    private String type;

    public Email(UUID email_id, UUID sender_id, List<UUID> recipientIds,
                 String subject, String body, OffsetDateTime sent_at, User sender, List<User> recipients,
                 String type) {
        this.emailId = email_id;
        this.senderId = sender_id;
        this.recipientIds = recipientIds;
        this.subject = subject;
        this.body = body;
        this.sentAt = sent_at;
        this.sender = sender;
        this.recipients = recipients;
        this.type = type;
    }

    public UUID getEmailId() {
        return emailId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public List<UUID> getRecipientIds() {
        return recipientIds;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }

    public User getSender() {
        return sender;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setRecipients(List<User> recipients) {
        this.recipients = recipients;
    }

    public void setSenderId(UUID senderId) {this.senderId = senderId;}

    public void setRecipientIds(List<UUID> recipientIds) {
        this.recipientIds = recipientIds;
    }

    public Boolean isInbox() {
        return type.equals("inbox");
    }
    public Boolean isOutbox(){
        return type.equals("outbox");
    }

}


