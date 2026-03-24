package client.service;

import client.model.User;
import client.network.TcpClient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SessionService {
    private ObjectProperty<User> currentUser = new SimpleObjectProperty<>();
    private ArrayList<Consumer<String>> sessionSetListeners = new ArrayList<>();

    public SessionService(TcpClient tcpClient) {
    }

    public void addListener(Consumer c)
    {
        sessionSetListeners.add(c);
    }

    public void setSession(User currentUser) {
        this.currentUser.set(currentUser);
        sessionSetListeners.forEach(c -> c.accept(""));
    }

    public ObjectProperty<User> getCurrentUser() {
        return currentUser;
    }


}
