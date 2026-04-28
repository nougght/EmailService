package client.network.request;

import java.util.List;
import java.util.UUID;

public class GetUsersRequest extends Request{
    private List<UUID> userIds;

    public GetUsersRequest() {super();}
    public GetUsersRequest(List<UUID> userIds){
        super("GetUsers");
        this.userIds = userIds;
    }

    public List<UUID> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<UUID> userIds) {
        this.userIds = userIds;
    }
}
