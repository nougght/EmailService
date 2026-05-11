package common.network.request;

import java.util.UUID;

public class DeleteDraftRequest extends Request{
    private UUID draftId;

    public DeleteDraftRequest() {super();}
    public DeleteDraftRequest(UUID draftId) {
        super("DeleteDraft");
        this.draftId = draftId;
    }

    public UUID getDraftId() {
        return draftId;
    }

    public void setDraftId(UUID draftId) {
        this.draftId = draftId;
    }
}
