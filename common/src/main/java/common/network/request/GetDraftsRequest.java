package common.network.request;

import java.util.UUID;

public class GetDraftsRequest extends Request{
    private UUID userId;
    public GetDraftsRequest() {super();}
    public GetDraftsRequest(UUID userId) {
        super("GetDrafts");
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
