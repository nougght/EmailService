package client.viewModel;

import client.model.Email;
import client.model.EmailSending;
import client.service.EmailService;
import client.service.SessionService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class EmailFormViewModel {
    private final EmailService emailService;
    private final SessionService sessionService;


    private StringProperty subject = new SimpleStringProperty();
    private StringProperty receiver = new SimpleStringProperty();
    private StringProperty body = new SimpleStringProperty();

    private StringProperty warning = new SimpleStringProperty();
    private BooleanProperty isWarningVisible = new SimpleBooleanProperty();

    private ObjectProperty<Object> onEmailSent = new SimpleObjectProperty<>();

    private ObservableList<String> recipients = FXCollections.observableArrayList();

    public EmailFormViewModel(EmailService emailService, SessionService sessionService) {
        this.emailService = emailService;
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
    }

//    public void setEmail(Email email) {
//        this.subject.set(email.getSubject());
//        this.receiver.set(email.getRecipients().getUsername());
//        this.body.set(email.getBody());
//    }

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
                        body.get()
                )
        ).thenAccept(e -> {
            if (e.isEmpty()) {
                Platform.runLater(()-> warning.set("Ошибка отправки"));
            } else {
                Platform.runLater(()-> onEmailSent.set(new Object()));
            }
        });
    }


}
