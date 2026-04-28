package client.network.response;

import client.dto.UserDTO;
import client.network.request.GetUsersRequest;

import java.util.Map;
import java.util.UUID;

public class GetUsersResponse extends Response {
    private Map<UUID, UserDTO> users;

    public GetUsersResponse() {
        super();
    }

    public GetUsersResponse(UUID requestId, Map<UUID, UserDTO> users, String status) {
        super(requestId, "GetUsers", status);
        this.users = users;

    }

    public Map<UUID, UserDTO> getUsers() {
        return users;
    }

    public void setUsers(Map<UUID, UserDTO> users) {
        this.users = users;
    }
}
