package server.network.response;

import server.network.request.LogoutRequest;

import java.util.UUID;

public class LogoutResponse extends Response {
    public LogoutResponse() {
        super();
    }

    public LogoutResponse(UUID requestId, String status) {
        super(requestId, "Logout", status);
    }
}
