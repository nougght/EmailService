package client.viewModel;

import java.util.ArrayList;
import java.util.UUID;

import client.model.EmailSending;
import client.service.DraftService;
import client.service.EmailService;
import client.service.SessionService;
import common.dto.Draft;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmailFormViewModel {
    private final EmailService emailService;
    private final DraftService draftService;
    private final SessionService sessionService;

    private StringProperty subject = new SimpleStringProperty();
    private StringProperty receiver = new SimpleStringProperty();
    private StringProperty body = new SimpleStringProperty();

    private StringProperty warning = new SimpleStringProperty();
    private BooleanProperty isWarningVisible = new SimpleBooleanProperty();

    private ObjectProperty<Object> onEmailSent = new SimpleObjectProperty<>();

    private ObservableList<String> recipients = FXCollections.observableArrayList();

    private Draft draft;

    public EmailFormViewModel(EmailService emailService, DraftService draftService, SessionService sessionService, Draft draft) {
        this.emailService = emailService;
        this.draftService = draftService;
        this.sessionService = sessionService;
//        setEmail(email);
        isWarningVisible.set(false);
        warning.addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                isWarningVisible.set(false);
            } else {
                isWarningVisible.set(true);
            }
        });
        setDraft(draft);
    }

    private void setDraft(Draft draft) {
        this.subject.set(draft.getSubject());
        this.recipients.addAll(draft.getRecipients());
        this.body.set(draft.getBody());
        this.draft = draft;
    }

    public void saveDraft(){
        System.out.println("saving draft");
        draft.setSubject(subject.get());
        draft.setBody(body.get());
        draft.setRecipients(recipients.stream().toList());
        draftService.updateDraft(draft);
    }

    public ObjectProperty<Object> getOnEmailSent(){
        return onEmailSent;
    }
    public StringProperty getBody() {
        return body;
    }

    public StringProperty getReceiver() {
        return receiver;
    }

    public StringProperty getSubject() {
        return subject;
    }

    public StringProperty getWarning() {
        return warning;
    }

    public BooleanProperty getIsWarningVisible() {
        return isWarningVisible;
    }

    public ObservableList<String> getRecipients() {
        return recipients;
    }
    public void onSendClicked() {
        if (recipients.isEmpty()){
            Platform.runLater(()-> warning.set("Получатель не может быть пустым"));
        }

        emailService.sendEmail(
                new EmailSending(
                        sessionService.getCurrentUser().get().getUserId(),
                        new ArrayList<>(recipients),
                        subject.get(),
                        body.get(),
                        draft.getDraftId()
                )
        ).thenAccept(e -> {
            if (e.isEmpty()) {
                Platform.runLater(()-> warning.set("Ошибка отправки"));
            } else {
                Platform.runLater(()-> {
                    draftService.deleteDraftLocal(draft.getDraftId());
                    onEmailSent.set(new Object());
                });
            }
        });
    }

}
