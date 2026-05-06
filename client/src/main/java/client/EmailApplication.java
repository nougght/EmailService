package client;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import client.network.TcpClient;
import client.service.AuthService;
import client.service.EmailService;
import client.service.NavigationService;
import client.service.SessionService;
import client.storage.DataStorage;
import common.network.notification.NewEmailNotification;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmailApplication extends Application {
    private final HashMap<String, Parent> pages = new HashMap<>();
    private final TcpClient tcpClient;
    private final DataStorage dataStorage;
    private AuthService authService;
    private EmailService emailService;
    private SessionService sessionService;
    private StackPane mainPane;

    public EmailApplication() {
        tcpClient = new TcpClient("localhost", 3741);
        dataStorage = new DataStorage();
    }


    @Override
    public void start(Stage stage) throws IOException {

        sessionService = new SessionService(tcpClient);
        // tcpClient должен получить access
        sessionService.addListener(_ -> tcpClient.setAccessToken(sessionService.getAccessToken()));
        authService = new AuthService(tcpClient, sessionService, dataStorage);
        emailService = new EmailService(
                tcpClient,
                sessionService,
                dataStorage
        );

        tcpClient.start();
        System.out.println("start session");

        NavigationService navigationService = new NavigationService(authService, sessionService, emailService, stage);

        authService.tryAutoAuth().thenAccept(s -> {
            if (s.equals("success")) {
                System.out.println("Successful auto auth");
                Platform.runLater(() -> navigationService.goToMainPage());

            } else {
                System.out.println("Auto auth failed: " + s);
                Platform.runLater(() -> navigationService.goToRegistrationPage());
            }
        });


        // вывод необработанных исключений в модальном окне
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {

            Stage modalStage = new Stage();
            modalStage.setTitle("Exception");

            modalStage.initModality(Modality.WINDOW_MODAL);

            modalStage.initOwner(stage);

            Label label = new Label(String.format("%s\nSTACK TRACE:\n%s", throwable.toString(), Arrays.toString(throwable.getStackTrace())));
            label.setWrapText(true);
            label.setFont(new Font(15));
            label.setMinWidth(300);
            Button closeButton = new Button("Close Modal");
            closeButton.setOnAction(e -> modalStage.close());

            VBox layout = new VBox();

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setMinHeight(400);
            scrollPane.setPrefWidth(400);
            layout.getChildren().addAll(scrollPane, closeButton);
            layout.setSpacing(10);
            layout.setStyle("-fx-padding: 20;");
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            scrollPane.setFitToWidth(true);
            scrollPane.setContent(label);
            scrollPane.setBorder(null);
            scrollPane.setPadding(new Insets(10));

            Scene modalScene = new Scene(layout, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

            modalStage.setScene(modalScene);

            Platform.runLater(() -> {
                scrollPane.setVvalue(0);
                modalStage.sizeToScene();
            });
            modalStage.showAndWait();
        });

        stage.show();
        // TODO move to another place
        new Thread(()-> {
            while (true) {
                try {
                    var ntf = tcpClient.getNotifications().take();
                    var type = ntf.getType();
                    switch (type) {
                        case "NewEmail" -> emailService.addEmail(((NewEmailNotification)ntf).getEmailDTO());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
