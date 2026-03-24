package server.network.response;

import server.dto.UserDTO;

import java.util.UUID;


public class GetUserResponse extends Response {
    private UserDTO userDTO;

    public GetUserResponse() {
        super();
    }

    public GetUserResponse(UUID requestId, String status, UserDTO userDTO) {
        super(requestId, "GetUser", status);
        this.userDTO = userDTO;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}