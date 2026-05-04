package server.model;

import java.util.Optional;
import java.util.UUID;

public class EmailRecipient {
    private UUID emailId;
    private UUID userId;
    private String username;

    public EmailRecipient() {}
    public EmailRecipient(UUID emailId, UUID userId, String username){
        this.emailId = emailId;
        this.userId = userId;
        this.username = username;
    }

    public UUID getEmailId() {
        return emailId;
    }
    public Optional<UUID> getUserId() {
        return Optional.of(userId);
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setEmailId(UUID emailId) {
        this.emailId = emailId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
