package client;

import client.mapper.EmailMapper;
import client.mapper.UserMapper;
import client.network.TcpClient;
import client.service.EmailService;
import client.service.SessionService;
import client.storage.DataStorage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.UUID;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }
}
