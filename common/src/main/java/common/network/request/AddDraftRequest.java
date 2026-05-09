package common.network.request;

import common.dto.Draft;

public class AddDraftRequest extends Request{
    private Draft draft;
    public AddDraftRequest() {super();}
    public AddDraftRequest(Draft draft){
        super("AddDraft");
        this.draft = draft;
    }

    public Draft getDraft() {
        return draft;
    }

    public void setDraft(Draft draft) {
        this.draft = draft;
    }
}
