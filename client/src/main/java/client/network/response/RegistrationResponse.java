package client.network.response;


import java.util.UUID;

public class RegistrationResponse extends Response{
    private String accessToken;
    private String refreshToken;

    public RegistrationResponse(){
        super();
    }
    public RegistrationResponse(UUID requestId, String status, String accessToken, String refreshToken)
    {
        super(requestId, "Registration", status);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    
}
