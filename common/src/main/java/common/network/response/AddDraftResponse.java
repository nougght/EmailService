package common.network.response;

import common.network.request.AddDraftRequest;

import java.util.UUID;

public class AddDraftResponse extends Response{
    private UUID draftId;
    public AddDraftResponse(){super();}
    public AddDraftResponse(UUID requestId, String status, UUID draftId){
        super(requestId, "AddDraft", status);
        this.draftId = draftId;
    }

    public UUID getDraftId() {
        return draftId;
    }

    public void setDraftId(UUID draftId) {
        this.draftId = draftId;
    }
}
