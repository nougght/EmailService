package server.network.request;

import server.network.response.RegistrationResponse;

import java.util.UUID;

public class RefreshRequest extends Request {
    private UUID userId;
    private String refreshToken;

    public RefreshRequest(){
        super();
    }
    public RefreshRequest(UUID userId,String refreshToken){
        super("Refresh");
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
