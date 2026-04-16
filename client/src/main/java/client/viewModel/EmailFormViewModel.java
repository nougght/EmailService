package client.viewModel;

import client.model.Email;
import client.service.EmailService;
import client.service.SessionService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EmailFormViewModel {
    private final EmailService emailService;
    private final SessionService sessionService;

    private final ObjectProperty<Email> email = new SimpleObjectProperty<>();

    private StringProperty subject = new SimpleStringProperty();
    private StringProperty receiver = new SimpleStringProperty();
    private StringProperty body = new SimpleStringProperty();

    private ObjectProperty<Email> onSendEmail = new SimpleObjectProperty<>();

    public EmailFormViewModel(EmailService emailService, SessionService sessionService,
                              Email email) {
        this.emailService = emailService;
        this.sessionService = sessionService;
        setEmail(email);

    }

    public void setEmail(Email email) {
        this.email.set(email);
        this.subject.set(email.getSubject());
        this.receiver.set(email.getReceiver().getUsername());
        this.body.set(email.getBody());

    }

    public void onSendClicked() {
        onSendEmail.set(email.get());
    }


}
