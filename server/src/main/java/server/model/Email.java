package server.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Email {
    private UUID emailId;
    private UUID senderId;
    private UUID receiverId;

    private String subject;
    private String body;
    private OffsetDateTime sentAt;

    private User sender;
    private User receiver;

    public Email(UUID email_id, UUID sender_id, UUID receiver_id,
                 String subject, String body, OffsetDateTime sent_at, User sender, User receiver) {
        this.emailId = email_id;
        this.senderId = sender_id;
        this.receiverId = receiver_id;
        this.subject = subject;
        this.body = body;
        this.sentAt = sent_at;
        this.sender = sender;
        this.receiver = receiver;
    }

    public UUID getEmailId() {
        return emailId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
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

    public User getReceiver() {
        return receiver;
    }

}
