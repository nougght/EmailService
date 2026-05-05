package client.viewModel;

import client.model.Email;
import client.model.User;
import client.service.AuthService;
import client.service.EmailService;
import client.service.SessionService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainViewModel {
    private final AuthService authService;
    private final EmailService emailService;
    private final SessionService sessionService;
    private ObjectProperty<User> currentUser;

    private FilteredList<Email> allEmails;
    private FilteredList<Email> inbox;
    private FilteredList<Email> outbox;
    private FilteredList<Email> drafts;

//    private List<FilteredList<Email>> tagEmails;


    private final Map<String, String> folderNames = new HashMap<>(Map.ofEntries(
            Map.entry("Все письма", "ALL"),
            Map.entry("Входящие", "INBOX"),
            Map.entry("Исходящие", "OUTBOX"),
            Map.entry("Черновики", "DRAFTS")
    ));


    public MainViewModel(AuthService authService, EmailService emailService, SessionService sessionService) {
        this.authService = authService;
        this.emailService = emailService;
        this.sessionService = sessionService;
        currentUser = sessionService.getCurrentUser();
        allEmails = emailService.getFolderEmails("ALL");
        inbox = emailService.getFolderEmails("INBOX");
        outbox = emailService.getFolderEmails("OUTBOX");
        drafts = emailService.getFolderEmails("DRAFTS");
    }

    private final ObjectProperty<Object> onLogout = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> onOpenEmail = new SimpleObjectProperty<>();
    private final ObjectProperty<Object> onNewEmail = new SimpleObjectProperty<>();


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

    public Map<String, String> getFolderNames() {
        return folderNames;
    }

    public FilteredList<Email> getFolderEmails(String folder) {
        switch (folder) {
            case "INBOX":
                return inbox;
            case "OUTBOX":
                return outbox;
            case "DRAFT":
                return drafts;
            case "ALL":
                return allEmails;
            default:
                return null;
        }
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
