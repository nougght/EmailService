package server.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Token {
    private UUID tokenId;
    private UUID userId;
    private String tokenHash;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;

    public Token(){}
    public Token(UUID tokenId, UUID userId, String tokenHash, OffsetDateTime createdAt, OffsetDateTime expiresAt)
    {
        this.tokenId = tokenId;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setTokenId(UUID tokenId) {
        this.tokenId = tokenId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
