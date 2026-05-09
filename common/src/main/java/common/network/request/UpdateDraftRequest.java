package common.network.request;

import common.dto.Draft;

public class UpdateDraftRequest extends Request{
    private Draft draft;

    public UpdateDraftRequest() {super();}
    public UpdateDraftRequest(Draft draft) {
        super("UpdateDraft");
        this.draft = draft;
    }

    public Draft getDraft() {
        return draft;
    }

    public void setDraft(Draft draft) {
        this.draft = draft;
    }
}
