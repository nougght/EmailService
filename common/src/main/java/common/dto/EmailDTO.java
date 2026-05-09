package common.dto;
import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;

//import client.DTOs.UserDTO;

public class EmailDTO {
    private UUID emailId;
    private UUID senderId;
    private String senderUsername;
    private List<EmailRecipientDTO> recipients;

    private String subject;
    private String body;
    private OffsetDateTime sentAt;

    private String folder;
    private boolean isRead;

    public EmailDTO(){}
    public EmailDTO(UUID email_id, UUID sender_id, List<EmailRecipientDTO> recipients,
                    String subject, String body, OffsetDateTime sent_at, String folder, boolean isRead) {
        this.emailId = email_id;
        this.senderId = sender_id;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.sentAt = sent_at;
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

    public List<EmailRecipientDTO> getRecipients() {
        return recipients;
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

    public String getFolder() {
        return folder;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setEmailId(UUID emailId) {
        this.emailId = emailId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public void setRecipients(List<EmailRecipientDTO> recipients) {
        this.recipients = recipients;
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

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
