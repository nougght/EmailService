package client.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import common.dto.EmailItem;
import common.dto.EmailRecipientDTO;

public class Email implements EmailItem {
    private UUID emailId;
    private UUID senderId;
    private String senderUsername;
    private String subject;
    private String body;
    private OffsetDateTime sentAt;

    private User sender;
    private List<EmailRecipientDTO> recipients;

    private String folder;
    private boolean isRead;

    public Email(UUID email_id, UUID sender_id, String senderUsername,
                 String subject, String body, OffsetDateTime sent_at, User sender, List<EmailRecipientDTO> recipients,
                 String folder, boolean isRead) {
        this.emailId = email_id;
        this.senderId = sender_id;
        this.senderUsername = senderUsername;
        this.subject = subject;
        this.body = body;
        this.sentAt = sent_at;
        this.sender = sender;
        this.recipients = recipients;
        this.folder = folder;
        this.isRead = isRead;
    }

    public UUID getEmailId() {
        return emailId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
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

    public List<EmailRecipientDTO> getRecipients() {
        return recipients;
    }

    public String getFolder() {
        return folder;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setRecipients(List<EmailRecipientDTO> recipients) {
        this.recipients = recipients;
    }

    public void setSenderId(UUID senderId) {this.senderId = senderId;}

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

//    // TEMP
//    public Boolean isInbox() {
//        return true;
//    }
//    public Boolean isOutbox(){
//        return false;
//    }

}

