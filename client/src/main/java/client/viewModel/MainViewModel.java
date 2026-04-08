package client.viewModel;

import client.model.Email;
import client.model.User;
import client.service.EmailService;
import client.service.SessionService;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

public class MainViewModel {
    final private EmailService emailService;
    final private SessionService sessionService;
    public MainViewModel(EmailService emailService, SessionService sessionService)
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
