package server.model;

import java.util.UUID;

public class UserEmail {
    private UUID userId;
    private String folder;
    private boolean isRead;

    public UserEmail(UUID userId, String folder, boolean isRead) {
        this.folder = folder;
        this.userId = userId;
        this.isRead = isRead;
    }

    public String getFolder() {
        return folder;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isRead() {
        return isRead;
    }
}
