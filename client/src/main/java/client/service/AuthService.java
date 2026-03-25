package client.service;

import client.network.TcpClient;
import client.storage.DataStorage;

public class AuthService {
    final private TcpClient tcpClient;
    final private SessionService sessionService;
    final private DataStorage storage;

    public AuthService(TcpClient tcpClient, SessionService sessionService, DataStorage storage)
    {
        this.tcpClient = tcpClient;
        this.sessionService = sessionService;
        this.storage = storage;
    }

    
}
