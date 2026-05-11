package common.network.response;

import java.util.UUID;

public class DeleteUserEmailResponse extends Response{
    public DeleteUserEmailResponse() {super();}
    public DeleteUserEmailResponse(UUID requestId, String status) {

        super(requestId, "DeleteUserEmail", status);
    }
}
