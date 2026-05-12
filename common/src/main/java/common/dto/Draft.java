package common.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Draft implements EmailItem{
    private UUID draftId;
    private UUID senderId;
    private List<String> recipients = new ArrayList<>();
    private String subject;
    private String body;
    private OffsetDateTime updatedAt;
    private OffsetDateTime createdAt;

    public Draft() {
        recipients = new ArrayList<>();
    }
    public Draft(UUID draftId, UUID senderId, List<String> recipients, String subject, String body,
                 OffsetDateTime updatedAt, OffsetDateTime createdAt) {
        this.draftId = draftId;
        this.senderId = senderId;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this. updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public UUID getDraftId() {
        return draftId;
    }

    public UUID getSenderId() {
        return senderId;
    }


    public String getBody() {
        return body;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public void setDraftId(UUID draftId) {
        this.draftId = draftId;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
