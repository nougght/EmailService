package client.viewModel;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class MainViewModel {
    private final AuthService authService;
    private final EmailService emailService;
    private final DraftService draftService;

    private final SessionService sessionService;
    private ObjectProperty<User> currentUser;

    private BooleanProperty isDeleteDisabled = new SimpleBooleanProperty(true);

    private FilteredList<? extends EmailItem> emails;
    private FilteredList<? extends EmailItem> drafts;

    private SortedList<? extends EmailItem> emailList;

    private String currentFolder;
    private int currentSort = 0;

//    private List<FilteredList<Email>> tagEmails;

    private final Map<String, String> folderNames = new HashMap<>(Map.ofEntries(
            Map.entry("Все письма", "ALL"),
            Map.entry("Входящие", "INBOX"),
            Map.entry("Исходящие", "OUTBOX"),
            Map.entry("Черновики", "DRAFTS")
    ));

    private final List<String> folderOrder = List.of(
            "Входящие", "Исходящие", "Черновики", "Все письма"
    );
    private final ObservableList<String> sortList = FXCollections.observableArrayList(List.of("Сначала новые",
            "Сначала старые"));


    public MainViewModel(AuthService authService, EmailService emailService, DraftService draftService, SessionService sessionService) {
        this.authService = authService;
        this.emailService = emailService;
        this.draftService = draftService;
        this.sessionService = sessionService;
        currentUser = sessionService.getCurrentUser();

        drafts = emailService.getDrafts();
        emails = emailService.getEmails();
        emailList = emails.sorted();
        currentFolder = "ALL";
    }

    private final ObjectProperty<Object> onLogout = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> onOpenEmail = new SimpleObjectProperty<>();
    private final ObjectProperty<Object> onNewEmail = new SimpleObjectProperty<>();
    private final ObjectProperty<Draft> onOpenDraft = new SimpleObjectProperty<Draft>();
    private final ObjectProperty<UUID> onDraftDelete = new SimpleObjectProperty<UUID>();



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

    public ObjectProperty<UUID> getOnDraftDelete() {
        return onDraftDelete;
    }

    public ObjectProperty<User> getCurrentUser() {
        return currentUser;
    }

    public Map<String, String> getFolderNames() {
        return folderNames;
    }

    public SortedList<? extends EmailItem> getEmailList() {
        return emailList;
    }

    public ObservableList<String> getSortList() {
        return sortList;
    }
    //    public FilteredList<? extends EmailItem> getFolderEmails(String folder) {
//        if (folder == null){
//            return new FilteredList<>(FXCollections.observableArrayList());
//        }
//        switch (folder) {
//            case "INBOX":
//                return inbox;
//            case "OUTBOX":
//                return outbox;
//            case "DRAFTS":
//                return drafts;
//            case "ALL":
//                return allEmails;
//            default:
//                return new FilteredList<>(FXCollections.observableArrayList());
//        }
//    }



    public void onDeleteClicked(EmailItem item) {
        System.out.println("delete clicked" + item.getSubject());
        if (item instanceof Email e) {
            emailService.deleteEmail(e);
        } else if (item instanceof Draft d) {
            draftService.deleteDraft(d.getDraftId());
            onDraftDelete.set(d.getDraftId());
        }
    }
    public void switchFolder(String folder) {
        if (folder == null){
            return;
        }
        switch (folder) {
            case "DRAFTS":
                if (!currentFolder.equals("DRAFTS")) {
                    emailList = drafts.sorted();
                    switchSort(currentSort);
                }
                break;
            default:
                if (currentFolder.equals("DRAFTS")){
                    emailList = emails.sorted();
                    switchSort(currentSort);
                }
                emails.setPredicate(e -> folder.equals("ALL") || ((Email) e).getFolder().equals(folder));
        }
        currentFolder = folder;
    }

    public void sortAscDate() {
        emailList.setComparator((e1, e2) -> {
            if (e1 instanceof Email email1 && e2 instanceof Email email2) {
                return email1.getSentAt().compareTo(email2.getSentAt());
            } else if (e1 instanceof Draft draft1 && e2 instanceof Draft draft2) {
                return draft1.getUpdatedAt().compareTo(draft2.getUpdatedAt());
            }
            return 0;
        });
    }

    public void sortDescDate() {
        emailList.setComparator((e1, e2) -> {
            if (e1 instanceof Email email1 && e2 instanceof Email email2) {
                return email2.getSentAt().compareTo(email1.getSentAt());
            } else if (e1 instanceof Draft draft1 && e2 instanceof Draft draft2) {
                return draft2.getUpdatedAt().compareTo(draft1.getUpdatedAt());
            }
            return 0;
        });
    }

    public void switchSort(int ind) {
        System.out.println("sort switched " + ind);
        switch (ind) {
            case 0:
                sortDescDate();
                break;
            case 1:
                sortAscDate();
                break;
        }
    }

    public List<String> getFolderOrder() {
        return folderOrder;
    }

    public void onRefreshClicked() {
        emailService.loadUserEmails(null);
        draftService.loadUserDrafts();
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
        onOpenDraft.set(null);
        onOpenDraft.set(draft);
    }

    public boolean isIsDeleteDisabled() {
        return isDeleteDisabled.get();
    }

    public BooleanProperty isDeleteDisabledProperty() {
        return isDeleteDisabled;
    }

    public void onEmailsListSelected(EmailItem item) {
        isDeleteDisabled.set(item == null);
    }

}
