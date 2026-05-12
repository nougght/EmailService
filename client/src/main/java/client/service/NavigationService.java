package client.service;

import client.EmailApplication;
import client.model.Email;
import client.view.*;
import client.viewModel.*;
import common.dto.Draft;
import common.dto.EmailRecipientDTO;
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

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NavigationService {
    private final AuthService authService;
    private final EmailService emailService;
    private final DraftService draftService;
    private SessionService sessionService;

    private final HashMap<String, Parent> views = new HashMap<>();

    private final Stage mainStage;
    private final ArrayList<Stage> windows = new ArrayList<>();
    private final Map<UUID, Stage> newEmailWindows = new HashMap<>();


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
        var base = getClass().getResource("/client/styles/base.css");
        var auth = getClass().getResource("/client/styles/auth.css");
        var mail = getClass().getResource("/client/styles/mail.css");
        var emailDetail = getClass().getResource("/client/styles/email-detail.css");
        if (base != null) {
            scene.getStylesheets().add(base.toExternalForm());
        }
        if (auth != null) {
            scene.getStylesheets().add(auth.toExternalForm());
        }
        if (mail != null) {
            scene.getStylesheets().add(mail.toExternalForm());
        }
        if (emailDetail != null) {
            scene.getStylesheets().add(emailDetail.toExternalForm());
        }
        mainStage.setTitle("Email Service");
        mainStage.setScene(scene);

    }

    private final ChangeListener<Object> onNewEmailListener = (_, _, _) -> {
        System.out.println("OnNewEmail Listener");
        addNewEmailWindow(null);
    };
    private final ChangeListener<Draft> onOpenDraftListener = (_, _, draft) -> {
        if (draft == null)
            return;
        addNewEmailWindow(draft);
    };

    // ответ на письмо
    private ChangeListener<Email> onReplyListener = (_, _, email) -> {
        var draft = new Draft();
        draft.setSenderId(sessionService.getCurrentUserId());
        // если письмо входящее, получатель - тот кто его прислал
        if (email.getFolder().equals("INBOX")) {
            draft.setRecipients(List.of(email.getSenderUsername()));
        } else {  // если исходящее, добавляем тех же получателей
            draft.setRecipients(email.getRecipients().stream().map(EmailRecipientDTO::getUsername).toList());
        }
        draft.setSubject(String.format("Re: %s", email.getSubject()));


        String body = String.format("-----\n%s, %s:\n%s",
                email.getSentAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd MM yyyy")),
                email.getSenderUsername(),
                email.getBody()
        );
        draft.setBody(String.format("\n\n\n%s", body));
        addNewEmailWindow(draft);
    };

    // пересылка письма
    private final ChangeListener<Email> onForwardListener = (_, _, email) -> {
        var draft = new Draft();
        draft.setSenderId(sessionService.getCurrentUserId());

        draft.setSubject(String.format("Fwd: %s", email.getSubject()));


        String body = String.format("-----\nFrom: %s\nDate: %s\nSubject:%s\nTo:%s\n\n%s",
                email.getSenderUsername(),
                email.getSentAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd MM yyyy")),
                email.getSubject(),
                email.getRecipients().stream()
                        .map(EmailRecipientDTO::getUsername)
                        .collect(Collectors.joining(", ")),
                email.getBody()
        );
        draft.setBody(String.format("\n\n\n%s", body));
        addNewEmailWindow(draft);
    };


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
                var vm = new MainViewModel(authService, emailService, draftService, sessionService);
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
                vm.getOnDraftDelete().addListener((o, ov, nv) -> {
                    closeNewEmailWindow(nv);
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
            emailVM.getOnReply().addListener(onReplyListener);
            emailVM.getOnForward().addListener(onForwardListener);
            contr.setViewModel(emailVM);
            splitPane.getItems().set(2, emailView);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeNewEmailWindow(UUID draftId) {
        var stage = newEmailWindows.get(draftId);
        windows.remove(stage);
        newEmailWindows.remove(draftId);
        stage.close();
    }

    public void addNewEmailWindow(Draft newDraft) {
        try {
            // ограничение открытия нескольких окон для одного черновика (не нового)
            if (newDraft != null && newEmailWindows.containsKey(newDraft.getDraftId())) {
                return;
            }
            System.out.println("addNewEmailWindow");
            Stage stage = new Stage();
            stage.initOwner(mainStage);

            var loader = new FXMLLoader(EmailApplication.class.getResource("email-form-view.fxml"));
            Parent emailFormView = loader.load();
            var contr = (EmailFormController) loader.getController();

            Consumer<Draft> openDraft = (draft) -> {
                EmailFormViewModel vm = new EmailFormViewModel(emailService, draftService, sessionService, draft);

                vm.getOnEmailSent().addListener((obj) -> {
                    closeNewEmailWindow(draft.getDraftId());
                });

                stage.setOnHidden(e -> {
                    vm.saveDraft();
                    closeNewEmailWindow(draft.getDraftId());
                });

                contr.setViewModel(vm);

                Scene scene = new Scene(emailFormView);
                var composeBase = EmailApplication.class.getResource("/client/styles/base.css");
                var compose = EmailApplication.class.getResource("/client/styles/compose.css");
                if (composeBase != null) {
                    scene.getStylesheets().add(composeBase.toExternalForm());
                }
                if (compose != null) {
                    scene.getStylesheets().add(compose.toExternalForm());
                }
                stage.setScene(scene);
                windows.add(stage);
                newEmailWindows.put(draft.getDraftId(), stage);
                stage.setY(mainStage.getY() + 200);
                stage.setX(mainStage.getX() + mainStage.getWidth() / 2 - 400);

                stage.show();
                stage.focusedProperty().addListener((obs, oldValue, newValue) -> {
                    if (!newValue) {
                        vm.saveDraft();
                    }
                });
            };

            if (newDraft == null) {  // создание нового пустого черновика
                newDraft = new Draft();
                newDraft.setSenderId(sessionService.getCurrentUser().get().getUserId());
                newDraft.setRecipients(new ArrayList<>());
                // TODO: offline draft creating and storing
                draftService.addDraft(newDraft).thenAccept(draft -> {
                    Platform.runLater(() -> {
                        openDraft.accept(draft);
                    });
                });

            } else if (newDraft.getDraftId() == null) {  // создание нового черновика с переданными данными (reply, forward)
                draftService.addDraft(newDraft).thenAccept(draft -> {
                    Platform.runLater(() -> {
                        openDraft.accept(draft);
                    });
                });
            } else {  // открытие уже существующего черновика
                openDraft.accept(newDraft);
            }

        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }

}
