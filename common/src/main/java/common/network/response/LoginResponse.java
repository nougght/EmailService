package common.network.response;

import java.util.UUID;

import common.dto.UserDTO;

public class LoginResponse extends Response{
    private UserDTO user;
    private String accessToken;
    private String refreshToken;

    public LoginResponse(){
        super();
    }
    public LoginResponse(UUID requestId, String status, UserDTO user, String accessToken, String refreshToken)
    {
        super(requestId, "Login", status);
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserDTO getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
