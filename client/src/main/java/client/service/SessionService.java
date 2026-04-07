package client.service;

import client.model.User;
import client.network.TcpClient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SessionService {
    private ObjectProperty<User> currentUser = new SimpleObjectProperty<>();
    private String accessToken;
    private String refreshToken;

    private ArrayList<Consumer<String>> sessionSetListeners = new ArrayList<>();

    public SessionService(TcpClient tcpClient) {
    }

    public void addListener(Consumer c)
    {
        sessionSetListeners.add(c);
    }

    public void setSession(User currentUser, String accessToken, String refreshToken) {
        this.currentUser.set(currentUser);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        sessionSetListeners.forEach(c -> c.accept(""));
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ObjectProperty<User> getCurrentUser() {
        return currentUser;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
