package client.model;

import client.dto.UserDTO;

public class AuthResult {
    private String status;
    private UserDTO user;
    private String accessToken;
    private String refreshToken;

    public AuthResult(String status, UserDTO user, String accessToken, String refreshToken)
    {
        this.status = status;
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getStatus() {
        return status;
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
