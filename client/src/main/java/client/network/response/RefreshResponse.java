package client.network.response;


import client.dto.UserDTO;

import java.util.UUID;

public class RefreshResponse extends Response{
    private UserDTO user;
    private String accessToken;

    public RefreshResponse(){super();}
    public RefreshResponse(UUID requestId, String status, UserDTO user, String accessToken)
    {
        super(requestId, "Refresh", status);
        this.user = user;
        this.accessToken = accessToken;
    }

    public UserDTO getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}