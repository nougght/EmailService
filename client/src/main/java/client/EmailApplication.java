package client;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import client.view.MainController;
import client.viewModel.MainViewModel;
import javafx.application.Platform;
import org.javatuples.Pair;

import client.network.TcpClient;
import client.service.AuthService;
import client.service.EmailService;
import client.service.SessionService;
import client.storage.DataStorage;
import client.view.LoginController;
import client.view.RegistrationController;
import client.viewModel.LoginViewModel;
import client.viewModel.RegistrationViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    public Pair<Parent, Object> getPageWithController(String name) throws IOException {
        if (pages.containsKey(name)) {
            return new Pair<Parent, Object>(pages.get(name), null);
        }
        var loader = new FXMLLoader(EmailApplication.class.getResource(name + ".fxml"));
        return new Pair<Parent, Object>(loader.load(), loader.getController());
    }

    public void goToLoginPage(StackPane root) {
        try {
            var pair = getPageWithController("login-view");
            Parent view = pair.getValue0();
            LoginController controller = (LoginController) pair.getValue1();
            if (controller != null)
            {
                var vm = new LoginViewModel(authService, sessionService);
                vm.getOnToRegistration().addListener((obj) -> {
                    goToRegistrationPage(mainPane);
                });
                vm.getOnLoggedIn().addListener((obj) -> {
                    goToMainPage(mainPane);
                });
                controller.setViewModel(vm);
            }
            root.getChildren().clear();
            root.getChildren().add(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void goToRegistrationPage(StackPane root) {
        try {
            var pair = getPageWithController("registration-view");
            Parent view = pair.getValue0();
            RegistrationController controller = (RegistrationController) pair.getValue1();
            if (controller != null)
            {
                var vm = new RegistrationViewModel(this.authService, sessionService);
                vm.getOnToLogin().addListener((obj) -> {
                    goToLoginPage(mainPane);
                });
                vm.getOnRegistered().addListener((obj) -> {
                    goToMainPage(mainPane);
                });
                controller.setViewModel(vm);
            }
            root.getChildren().clear();
            root.getChildren().add(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void goToMainPage(StackPane root){
        try {
            var pair = getPageWithController("main-view");
            Parent view = pair.getValue0();
            MainController controller = (MainController) pair.getValue1();
            if (controller != null)
            {
                var vm = new MainViewModel(emailService, sessionService);
                controller.setViewModel(vm);
            }
            root.getChildren().clear();
            root.getChildren().add(view);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage stage) throws IOException {

        sessionService = new SessionService(tcpClient);

        authService = new AuthService(tcpClient, sessionService, dataStorage);
        emailService = new EmailService(
                tcpClient,
                sessionService,
                dataStorage
        );

        tcpClient.start();
        // temp
        System.out.println("start session");
        emailService.getUserByUserId(UUID.fromString("94c33924-fe82-46b1-9d2b-84942a7da794")).
                thenAccept(u -> {
                    dataStorage.addUser(u.get());
                    sessionService.setSession(u.get(), null, null);
                });

//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
//        Parent root = fxmlLoader.load();
//        HelloController controller = fxmlLoader.getController();
//        controller.setViewModel(new EmailViewModel(emailService, sessionService));


        mainPane = new StackPane();
        Scene scene = new Scene(mainPane, 1000, 700);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        authService.tryAutoAuth().thenAccept(s -> {
            if (s.equals("success")){
                System.out.println("Successful auto auth");
                Platform.runLater(() -> goToMainPage(mainPane));

            }
            else {
                System.out.println("Auto auth failed: " + s);
                Platform.runLater(() -> goToRegistrationPage(mainPane));
            }
        });
//        goToRegistrationPage(mainPane);


        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // лог + показать ошибку
            Stage modalStage = new Stage();
            modalStage.setTitle("Exception");

            // 1. Set the modality to block the owner window
            modalStage.initModality(Modality.WINDOW_MODAL);
            // 2. Set the owner (optional but recommended)
            modalStage.initOwner(stage);

            Label label = new Label(throwable.toString());
            label.setWrapText(true);
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
