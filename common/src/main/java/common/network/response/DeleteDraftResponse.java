package common.network.response;

import java.util.UUID;

public class DeleteDraftResponse extends Response{
    public DeleteDraftResponse() {super();}
    public DeleteDraftResponse(UUID requestId, String status) {

        super(requestId, "DeleteDraft", status);
    }
}
