package client.viewModel;

import client.model.Email;
import client.service.EmailService;
import client.service.SessionService;
import common.dto.EmailRecipientDTO;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmailViewModel {
    private final EmailService emailService;
    private final SessionService sessionService;

    public Email email;

    private StringProperty subject = new SimpleStringProperty();
    private StringProperty sender = new SimpleStringProperty();
    private ObservableList<EmailRecipientDTO> recipients = FXCollections.observableArrayList();
    private StringProperty body = new SimpleStringProperty();
    private BooleanProperty isRead = new SimpleBooleanProperty();

    private ObjectProperty<Email> onReply = new SimpleObjectProperty<>();
    private ObjectProperty<Email> onForward = new SimpleObjectProperty<>();

    public EmailViewModel(EmailService emailService, SessionService sessionService, Email email){
        this.emailService = emailService;
        this.sessionService = sessionService;
        setEmail(email);
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
        subject.set(email.getSubject());
        sender.set(email.getSender().getUsername());
        recipients.addAll(email.getRecipients());
        body.set(email.getBody());
        isRead.set(email.isRead());
    }

    public StringProperty getSubject() {
        return subject;
    }

    public StringProperty getSender() {
        return sender;
    }

    public ObservableList<EmailRecipientDTO> getRecipients() {
        return recipients;
    }

    public StringProperty getBody() {
        return body;
    }

    public ObjectProperty<Email> getOnReply() {
        return onReply;
    }

    public ObjectProperty<Email> getOnForward() {
        return onForward;
    }

    public void onReplyClicked() {
        onReply.set(email);
    }

    public void onForwardClicked() {
        onForward.set(email);
    }
}
