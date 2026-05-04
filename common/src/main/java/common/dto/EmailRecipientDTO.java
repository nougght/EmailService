package common.dto;

import java.util.Optional;
import java.util.UUID;

public class EmailRecipientDTO {
    private UUID userId;
    private String username;

    public EmailRecipientDTO() {
    }

    public EmailRecipientDTO(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
