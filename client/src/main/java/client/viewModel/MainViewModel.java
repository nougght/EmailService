package client.viewModel;

import client.model.AuthResult;
import client.model.Email;
import client.model.User;
import client.service.AuthService;
import client.service.EmailService;
import client.service.SessionService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

public class MainViewModel {
    final private AuthService authService;
    final private EmailService emailService;
    final private SessionService sessionService;
    public MainViewModel(AuthService authService, EmailService emailService, SessionService sessionService)
    {
        this.authService = authService;
        this.emailService = emailService;
        this.sessionService = sessionService;
        currentUser = sessionService.getCurrentUser();
        emails = emailService.getEmailsList();
    }

    private ObjectProperty<Object> onLogout = new SimpleObjectProperty<>();

    private ObjectProperty<User> currentUser;
    private ObservableList<Email> emails;

    public ObjectProperty<User> getCurrentUser() {
        return currentUser;
    }

    public ObjectProperty<Object> getOnLogout()
    {
        return onLogout;
    }

    public ObservableList<Email> getEmails() {
        return emails;
    }

    public void onRefreshClicked()
    {
        emailService.loadUserEmails(null);
    }

    public void onLogoutClicked()
    {
        authService.logout().thenAccept(s -> {
            if (s.equals("success")) {
                onLogout.set(new Object());
            }
        });
    }
}
