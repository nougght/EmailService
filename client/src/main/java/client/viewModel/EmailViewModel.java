package client.viewModel;

import client.model.Email;
import client.service.EmailService;
import client.service.SessionService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EmailViewModel {
    private final EmailService emailService;
    private final SessionService sessionService;

    public Email email;

    private StringProperty subject = new SimpleStringProperty();
    private StringProperty sender = new SimpleStringProperty();
    private StringProperty receiver = new SimpleStringProperty();
    private StringProperty body = new SimpleStringProperty();

    private ObjectProperty<Object> onReply = new SimpleObjectProperty<>();
    private ObjectProperty<Object> onForward = new SimpleObjectProperty<>();


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
        receiver.set(email.getReceiver().getUsername());
        body.set(email.getBody());
    }

    public StringProperty getSubject() {
        return subject;
    }

    public StringProperty getSender() {
        return sender;
    }

    public StringProperty getReceiver() {
        return receiver;
    }

    public StringProperty getBody() {
        return body;
    }

    public void onReplyClicked() {
        onReply.set(new Object());
    }

    public void onForwardClicked() {
        onForward.set(new Object());
    }
}
