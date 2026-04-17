package client.viewModel;

import client.model.Email;
import client.model.User;
import client.service.AuthService;
import client.service.EmailService;
import client.service.SessionService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import java.util.UUID;

public class MainViewModel {
    private final AuthService authService;
    private final EmailService emailService;
    private final SessionService sessionService;

    public MainViewModel(AuthService authService, EmailService emailService, SessionService sessionService) {
        this.authService = authService;
        this.emailService = emailService;
        this.sessionService = sessionService;
        currentUser = sessionService.getCurrentUser();
        emails = emailService.getEmailsList();
    }

    private final ObjectProperty<Object> onLogout = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> onOpenEmail = new SimpleObjectProperty<>();
    private final ObjectProperty<Object> onNewEmail = new SimpleObjectProperty<>();

    private ObjectProperty<User> currentUser;
    private ObservableList<Email> emails;

    public ObjectProperty<Object> getOnLogout() {
        return onLogout;
    }

    public ObjectProperty<UUID> getOnOpenEmail() {
        return onOpenEmail;
    }
    public ObjectProperty<Object> getOnNewEmail() {
        return onNewEmail;
    }

    public ObjectProperty<User> getCurrentUser() {
        return currentUser;
    }

    public ObservableList<Email> getEmails() {
        return emails;
    }

    public void onRefreshClicked() {
        emailService.loadUserEmails(null);
    }

    public void onLogoutClicked() {
        authService.logout().thenAccept(s -> {
            if (s.equals("success")) {
                onLogout.set(new Object());
            }
        });
    }

    public void onEmailClicked(UUID emailId) {
        onOpenEmail.set(emailId);
    }

    public void onNewEmailClicked() {
        System.out.println("onNewEmailClicked");

        onNewEmail.set(new Object());
    }
}
