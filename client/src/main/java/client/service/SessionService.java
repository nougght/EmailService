package client.service;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import client.model.User;
import client.network.TcpClient;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SessionService {
    final private ObjectProperty<User> currentUser = new SimpleObjectProperty<>();
    private String accessToken;
    private String refreshToken;
    private UUID savedUserId;


    final private StandardPBEStringEncryptor encryptor;

    private ArrayList<Consumer<String>> sessionSetListeners = new ArrayList<>();

    public SessionService(TcpClient tcpClient) {
        encryptor  = new StandardPBEStringEncryptor();
        // to do: hide password
        encryptor.setPassword("49Kst0rE/701");
        encryptor.setAlgorithm("PBEWithMD5AndDES"); // Use default
    }

    public void addListener(Consumer c)
    {
        sessionSetListeners.add(c);
    }

    public void setSession(User currentUser, String accessToken, String refreshToken) {
        this.currentUser.set(currentUser);
        this.accessToken = accessToken;
        // done: сохранение refresh token на клиенте
        try {
            if (refreshToken != null) {
                this.refreshToken = refreshToken;
                Preferences prefs = Preferences.userNodeForPackage(Application.class);

                prefs.put("refresh_token", encryptor.encrypt(refreshToken));
                prefs.put("user_id", currentUser.getUserId().toString());
                System.out.println("Refresh token saved");
            }
        } catch (Exception e) {
            System.err.println("Error encrypting refresh token: " + e.getMessage());
            throw new RuntimeException(e);
        }
        sessionSetListeners.forEach(c -> c.accept(""));
    }

    public void LoadRefreshTokenAndUserId()
    {
        Preferences prefs = Preferences.userNodeForPackage(Application.class);
        String encryptedRefreshToken = prefs.get("refresh_token", null);
        if (encryptedRefreshToken != null) {
            this.refreshToken = encryptor.decrypt(encryptedRefreshToken);
            System.out.println("Refresh token loaded " + (this.refreshToken == null ? null : this.refreshToken.substring(0, 10)));
        }
        var userId = prefs.get("user_id", null);
        if (userId != null) {
            this.savedUserId = UUID.fromString(userId);
        }

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

    public UUID getSavedUserId() {
        return savedUserId;
    }
}
