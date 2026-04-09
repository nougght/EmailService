package client.service;

import client.mapper.UserMapper;
import client.network.TcpClient;
import client.storage.DataStorage;

import java.util.concurrent.CompletableFuture;

public class AuthService {
    final private TcpClient tcpClient;
    final private SessionService sessionService;
    final private DataStorage storage;

    public AuthService(TcpClient tcpClient, SessionService sessionService, DataStorage storage) {
        this.tcpClient = tcpClient;
        this.sessionService = sessionService;
        this.storage = storage;
    }


    public CompletableFuture<String> tryAutoAuth(){
        sessionService.LoadRefreshTokenAndUserId();
        if (sessionService.getRefreshToken() == null || sessionService.getSavedUserId() == null)
            return CompletableFuture.completedFuture("failure");
        return tcpClient.requestAutoAuth(sessionService.getRefreshToken(), sessionService.getSavedUserId()).thenApply(res -> {
            if (res.getStatus().equals("success")) {
                sessionService.setSession(
                        UserMapper.fromDTO(res.getUser()), res.getAccessToken(), res.getRefreshToken());
                tcpClient.setAccessToken(res.getAccessToken());
            }
            return res.getStatus();
        });
    }
    public CompletableFuture<String> register(String username, String password) {
        return tcpClient.requestRegistration(username, password).thenApply(res -> {
            if (res.getStatus().equals("success")){
                sessionService.setSession(
                        UserMapper.fromDTO(res.getUser()), res.getAccessToken(), res.getRefreshToken());
                tcpClient.setAccessToken(res.getAccessToken());

            }
            return res.getStatus();
        });
    }

    public CompletableFuture<String> login(String username, String password) {
        return tcpClient.requestLogin(username, password).thenApply(res -> {
            if (res.getStatus().equals("success")){
                tcpClient.setAccessToken(res.getAccessToken());
                sessionService.setSession(
                        UserMapper.fromDTO(res.getUser()), res.getAccessToken(), res.getRefreshToken());

            }
            return res.getStatus();
        });
    }


    public CompletableFuture<String> logout() {
        return tcpClient.requestLogout(sessionService.getCurrentUser().get().getUserId()).thenApply(s -> {
            if (s.equals("success")){
                tcpClient.setAccessToken(null);
                sessionService.resetSession();
                System.out.println("Logged out");
            }
            return s;
        });
    }


}
