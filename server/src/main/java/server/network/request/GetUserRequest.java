package server.network.request;

import java.util.UUID;

public class GetUserRequest extends Request {
    private UUID userId;

    public GetUserRequest(){
        super();
    }

    public GetUserRequest(UUID userId) {
        super("GetUser");
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
