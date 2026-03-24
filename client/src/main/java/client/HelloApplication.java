package client;

import client.mapper.EmailMapper;
import client.mapper.UserMapper;
import client.network.TcpClient;
import client.service.EmailService;
import client.service.SessionService;
import client.storage.DataStorage;
import client.view.EmailController;
import client.viewModel.EmailViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        TcpClient tcpClient = new TcpClient("127.0.0.1", 3741);
        DataStorage storage = new DataStorage();
        SessionService sessionService = new SessionService(tcpClient);
        EmailMapper emailMapper = new EmailMapper();
        UserMapper userMapper = new UserMapper();

        EmailService emailService = new EmailService(
                tcpClient,
                sessionService,
                storage,
                emailMapper,
                userMapper
        );

        tcpClient.start();
        // temp
        System.out.println("start session");
        emailService.getUserByUserId(UUID.fromString("94c33924-fe82-46b1-9d2b-84942a7da794")).
                thenAccept(u -> {storage.addUser(u.get()); sessionService.setSession(u.get());});


        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        HelloController controller = fxmlLoader.getController();
        controller.setViewModel(new EmailViewModel(emailService, sessionService));

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // лог + показать ошибку
            Stage modalStage = new Stage();
            modalStage.setTitle("Exception");

            // 1. Set the modality to block the owner window
            modalStage.initModality(Modality.WINDOW_MODAL);
            // 2. Set the owner (optional but recommended)
            modalStage.initOwner(stage);

            Label label = new Label(throwable.toString());
            Button closeButton = new Button("Close Modal");
            closeButton.setOnAction(e -> modalStage.close());

            VBox layout = new VBox(10);
            layout.getChildren().addAll(label, closeButton);
            layout.setStyle("-fx-padding: 20;");
            Scene modalScene = new Scene(layout, 300, 150);

            modalStage.setScene(modalScene);

            // 3. Show the window and wait
            modalStage.showAndWait();
        });

        stage.show();
    }
}
