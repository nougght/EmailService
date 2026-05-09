package common.network.response;

import common.network.request.UpdateDraftRequest;

import java.util.UUID;

public class UpdateDraftResponse extends Response{
    public UpdateDraftResponse() {super();}
    public UpdateDraftResponse(UUID requestId, String status) {
        super(requestId, "UpdateDraft", status);
    }
}
