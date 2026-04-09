package client.network.response;


import client.dto.UserDTO;

import java.util.UUID;

public class RegistrationResponse extends Response{
    private UserDTO user;
    private String accessToken;
    private String refreshToken;

    public RegistrationResponse(){
        super();
    }
    public RegistrationResponse(UUID requestId, String status, UserDTO user, String accessToken, String refreshToken)
    {
        super(requestId, "Registration", status);
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