package client.network.response;

import java.util.UUID;

public class LogoutResponse extends Response{
    public LogoutResponse(){super();}
    public LogoutResponse(UUID requestId, String status){
        super(requestId, "Logout", status);
    }
}
