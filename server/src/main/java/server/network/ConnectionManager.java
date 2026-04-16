package server.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final Map<UUID, ArrayList<ClientHandler>> clients = new ConcurrentHashMap<>();

    public void addClient(UUID userId, ClientHandler handler) {
        var lst = clients.get(userId);
        if (lst != null) {
            lst.add(handler);
        } else {
            clients.put(userId, new ArrayList<ClientHandler>(List.of(handler)));
        }
    }

    public void removeClient(UUID userId) {
        clients.remove(userId);
    }

    public void removeHandler(UUID userId, ClientHandler handler) {
        var lst = clients.get(userId);
        if (lst != null) {
            lst.remove(handler);
        }
    }


    public ArrayList<ClientHandler> get(UUID userId) {
        return clients.get(userId);
    }
}
