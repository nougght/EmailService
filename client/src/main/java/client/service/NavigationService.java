package client.service;

import client.EmailApplication;
import client.view.EmailController;
import client.view.LoginController;
import client.view.MainController;
import client.view.RegistrationController;
import client.viewModel.EmailViewModel;
import client.viewModel.LoginViewModel;
import client.viewModel.MainViewModel;
import client.viewModel.RegistrationViewModel;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class NavigationService {
    private final AuthService authService;
    private final SessionService sessionService;
    private final EmailService emailService;

    private final HashMap<String, Parent> views = new HashMap<>();

    private final Stage mainStage;
    private final ArrayList<Stage> windows = new ArrayList<>();
    private StackPane rootPane;
    private BorderPane borderPane;

    public NavigationService(AuthService authService, SessionService sessionService, EmailService emailService, Stage mainStage){
        this.authService = authService;
        this.sessionService = sessionService;
        this.emailService = emailService;
        this.mainStage = mainStage;
        rootPane = new StackPane();
        Scene scene = new Scene(rootPane, 1000, 700);
//        scene.getStylesheets().add(getClass().getResource("/client/styles/base.css").toExternalForm());
        mainStage.setTitle("Email Service");
        mainStage.setScene(scene);
    }

    public Pair<Parent, Object> getOrCreateView(String name) {
        try {
            if (views.containsKey(name)) {
                return new Pair<>(views.get(name), null);
            }
            var path = String.format("%s.fxml", name);
            var loader = new FXMLLoader(EmailApplication.class.getResource(path));
            return new Pair<>(loader.load(), loader.getController());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void goToLoginPage(){
        try {
            var pair = getOrCreateView("login-view");
            Parent view = pair.getValue0();
            LoginController controller = (LoginController) pair.getValue1();
            if (controller != null) {
                var vm = new LoginViewModel(authService, sessionService);
                vm.getOnToRegistration().addListener((obj) -> {
                    goToRegistrationPage();
                });
                vm.getOnLoggedIn().addListener((obj) -> {
                    goToMainPage();
                });
                controller.setViewModel(vm);
            }
            rootPane.getChildren().clear();
            rootPane.getChildren().add(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void goToRegistrationPage() {
        try {
            var pair = getOrCreateView("registration-view");
            Parent view = pair.getValue0();
            RegistrationController controller = (RegistrationController) pair.getValue1();
            if (controller != null) {
                var vm = new RegistrationViewModel(this.authService, sessionService);
                vm.getOnToLogin().addListener((obj) -> {
                    goToLoginPage();
                });
                vm.getOnRegistered().addListener((obj) -> {
                    goToMainPage();
                });
                controller.setViewModel(vm);
            }
            rootPane.getChildren().clear();
            rootPane.getChildren().add(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void goToMainPage() {
        try {
            var pair = getOrCreateView("main-view");
            Parent view = pair.getValue0();
            MainController controller = (MainController) pair.getValue1();
            borderPane = controller.getBorderPane();
            if (controller != null) {
                var vm = new MainViewModel(authService, emailService, sessionService);
                vm.getOnOpenEmail().addListener( (ObservableValue<? extends UUID> obs, UUID old, UUID id) -> {
                    showEmailView(id);
                });
                vm.getOnLogout().addListener(obj -> {
                    Platform.runLater(() -> {
                        goToLoginPage();
                    });
                });
                controller.setViewModel(vm);
            }
            rootPane.getChildren().clear();
            rootPane.getChildren().add(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void showEmailView(UUID emailId) {
        try {
            var loader = new FXMLLoader(EmailApplication.class.getResource("email-vdfiew.fxml"));

            Parent emailView = loader.load();
            var contr = (EmailController)loader.getController();
            var optionalEmail = emailService.getEmailByEmailId(emailId);
            if (optionalEmail.isEmpty())
                return;
            EmailViewModel emailVM = new EmailViewModel(emailService, sessionService, optionalEmail.get());
            // to do: reply and forward buttons handling
            contr.setViewModel(emailVM);
            borderPane.setCenter(emailView);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewEmailWindow() {
        try {
            Stage stage = new Stage();
            var loader = new FXMLLoader(EmailApplication.class.getResource("email-form-view.fxml"));
            // temp
            Parent emailView = loader.load();
            var contr = (EmailController) loader.getController();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
