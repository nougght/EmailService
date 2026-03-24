package server.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

//import client.DTOs.UserDTO;


public class EmailDTO {
    private UUID emailId;
    private UUID senderId;
    private UUID receiverId;

    private String subject;
    private String body;
    private OffsetDateTime sentAt;

    public EmailDTO(){}
    public EmailDTO(UUID email_id, UUID sender_id, UUID receiver_id,
                    String subject, String body, OffsetDateTime sent_at) {
        this.emailId = email_id;
        this.senderId = sender_id;
        this.receiverId = receiver_id;
        this.subject = subject;
        this.body = body;
        this.sentAt = sent_at;
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

    public void setEmailId(UUID emailId) {
        this.emailId = emailId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSentAt(OffsetDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
