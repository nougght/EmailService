package client.viewModel;

import client.model.Email;
import client.model.User;
import client.service.EmailService;
import client.service.SessionService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

public class EmailViewModel {
    final private EmailService emailService;
    final private SessionService sessionService;
    public EmailViewModel(EmailService emailService, SessionService sessionService)
    {
        this.emailService = emailService;
        this.sessionService = sessionService;
        currentUser = sessionService.getCurrentUser();
        emails = emailService.getEmailsList();
    }


    private ObjectProperty<User> currentUser;
    private ObservableList<Email> emails;

    public ObjectProperty<User> getCurrentUser() {
        return currentUser;
    }

    public ObservableList<Email> getEmails() {
        return emails;
    }

    public void onRefreshClicked()
    {
        emailService.loadUserEmails(null);
    }
}
