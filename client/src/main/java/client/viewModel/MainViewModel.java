package client.viewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import client.model.Email;
import client.model.User;
import client.service.AuthService;
import client.service.DraftService;
import client.service.EmailService;
import client.service.SessionService;
import common.dto.Draft;
import common.dto.EmailItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;

public class MainViewModel {
    private final AuthService authService;
    private final EmailService emailService;
    private final SessionService sessionService;
    private ObjectProperty<User> currentUser;

    private FilteredList<? extends EmailItem> allEmails;
    private FilteredList<? extends EmailItem> inbox;
    private FilteredList<? extends EmailItem> outbox;
    private FilteredList<? extends EmailItem> drafts;

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
    private final ObjectProperty<Draft> onOpenDraft = new SimpleObjectProperty<Draft>();

    public ObjectProperty<Object> getOnLogout() {
        return onLogout;
    }

    public ObjectProperty<UUID> getOnOpenEmail() {
        return onOpenEmail;
    }

    public ObjectProperty<Object> getOnNewEmail() {
        return onNewEmail;
    }

    public ObjectProperty<Draft> getOnOpenDraft() {
        return onOpenDraft;
    }

    public ObjectProperty<User> getCurrentUser() {
        return currentUser;
    }

    public Map<String, String> getFolderNames() {
        return folderNames;
    }

    public FilteredList<? extends EmailItem> getFolderEmails(String folder) {
        switch (folder) {
            case "INBOX":
                return inbox;
            case "OUTBOX":
                return outbox;
            case "DRAFTS":
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

    public void onDraftClicked(Draft draft) {
        onOpenDraft.set(draft);
    }
}
