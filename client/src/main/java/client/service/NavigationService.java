package client.service;

import client.EmailApplication;
import client.view.*;
import client.viewModel.*;
import common.dto.Draft;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class NavigationService {
    private final AuthService authService;
    private final SessionService sessionService;
    private final EmailService emailService;
    private final DraftService draftService;

    private final HashMap<String, Parent> views = new HashMap<>();

    private final Stage mainStage;
    private final ArrayList<Stage> windows = new ArrayList<>();
    private final ChangeListener<Object> onNewEmailListener = (_, _, _) -> {
        System.out.println("OnNewEmail Listener");
        addNewEmailWindow(null);
    };
    private final ChangeListener<Draft> onOpenDraftListener = (_, _, draft) -> {
        addNewEmailWindow(draft);
    };


private StackPane rootPane;
private SplitPane splitPane;

public NavigationService(AuthService authService, SessionService sessionService, EmailService emailService,
                         DraftService draftService, Stage mainStage) {
    this.authService = authService;
    this.sessionService = sessionService;
    this.emailService = emailService;
    this.draftService = draftService;
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

public void goToLoginPage() {
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
        splitPane = controller.getSplitPane();
        if (controller != null) {
            var vm = new MainViewModel(authService, emailService, sessionService);
            vm.getOnNewEmail().addListener(onNewEmailListener);
            vm.getOnOpenDraft().addListener(onOpenDraftListener);
            vm.getOnOpenEmail().addListener((ObservableValue<? extends UUID> obs, UUID old, UUID id) -> {
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
        var loader = new FXMLLoader(EmailApplication.class.getResource("email-view.fxml"));

        Parent emailView = loader.load();
        var contr = (EmailController) loader.getController();
        var optionalEmail = emailService.getEmailByEmailId(emailId);
        if (optionalEmail.isEmpty())
            return;
        EmailViewModel emailVM = new EmailViewModel(emailService, sessionService, optionalEmail.get());
        // to do: reply and forward buttons handling
        contr.setViewModel(emailVM);
        splitPane.getItems().set(2, emailView);

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

public void addNewEmailWindow(Draft newDraft) {
    try {
        System.out.println("addNewEmailWindow");
        Stage stage = new Stage();
        stage.initOwner(mainStage);

        var loader = new FXMLLoader(EmailApplication.class.getResource("email-form-view.fxml"));
        Parent emailFormView = loader.load();
        var contr = (EmailFormController) loader.getController();

        Consumer<Draft> openDraft = (draft) -> {
            EmailFormViewModel vm = new EmailFormViewModel(emailService, draftService, sessionService, draft);

            vm.getOnEmailSent().addListener((obj) -> {
                stage.close();
            });
            stage.onCloseRequestProperty().addListener(_ -> {
                vm.saveDraft();
                windows.remove(stage);
            });
            contr.setViewModel(vm);

            Scene scene = new Scene(emailFormView);
            stage.setScene(scene);
            windows.add(stage);
            stage.show();
            stage.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue) {
                    vm.saveDraft();
                }
            });
        };

        if (newDraft == null) {
            newDraft = new Draft();
            newDraft.setSenderId(sessionService.getCurrentUser().get().getUserId());
            newDraft.setRecipients(new ArrayList<>());
            // TODO: offline draft creating and storing
            draftService.addDraft(newDraft).thenAccept(draft -> {
                Platform.runLater(() -> {
                    openDraft.accept(draft);
                });
            });

        } else {
            openDraft.accept(newDraft);
        }

    } catch (Exception e) {
        System.out.println(e.toString());
        throw new RuntimeException(e);
    }
}

}
