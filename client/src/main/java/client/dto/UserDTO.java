package client.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserDTO {
    private UUID userId;
    private String username;
    private String email;
    private OffsetDateTime createdAt;

    public UserDTO(){}
    public UserDTO(UUID user_id, String username, String email, OffsetDateTime created_at) {
        this.userId = user_id;
        this.username = username;
        this.email = email;
        this.createdAt = created_at;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
