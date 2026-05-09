package common.network.response;

import common.dto.Draft;

import java.util.List;
import java.util.UUID;

public class GetDraftsResponse extends Response{
    private UUID userId;
    private List<Draft> drafts;
    public GetDraftsResponse(){super();}
    public GetDraftsResponse(UUID requestId, String status, UUID userId, List<Draft> drafts){
        super(requestId, "GetDrafts", status);
        this.drafts = drafts;
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<Draft> getDrafts() {
        return drafts;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setDrafts(List<Draft> drafts) {
        this.drafts = drafts;
    }
}
