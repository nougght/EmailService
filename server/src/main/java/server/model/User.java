package server.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {
    private UUID userId;
    private String username;
    private String passwordHash;
    private String email;
    private OffsetDateTime createdAt;

    public User(UUID user_id, String username, String passwordHash, String email, OffsetDateTime created_at) {
        this.userId = user_id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.createdAt = created_at;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }


}
