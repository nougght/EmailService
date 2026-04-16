package client.storage;

import client.dto.EmailDTO;
import client.dto.UserDTO;
import client.model.Email;
import client.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// хранилище, предоставляющее доступ к данным, но без логики обработки
public class DataStorage {
    final private ObservableList<Email> emails = FXCollections.observableArrayList();
    final private ObservableList<User> users = FXCollections.observableArrayList();


//    emails

    public Optional<Email> getEmailByEmailId(UUID emailId) {
        return emails.stream().filter(e -> e.getEmailId() == emailId).findFirst();
    }

    public ArrayList<Email> getEmailsBySenderId(UUID senderId) {
        return emails.stream().filter(e -> e.getSenderId() == senderId).
                collect(Collectors.toCollection(ArrayList::new));
    }

    public ObservableList<Email> getAllEmails()
    {
        return emails;
    }

    public ArrayList<Email> getEmailsByReceiverId(UUID receiverId) {
        return emails.stream().filter(e -> e.getReceiverId() == receiverId).
                collect(Collectors.toCollection(ArrayList::new));
    }

//    users

    public Optional<User> getUserByUserId(UUID userId) {
        return users.stream().filter(u -> u.getUserId().equals(userId)).findFirst();
    }

    public Optional<User> getUserByUsername(String username)
    {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    public void setAllEmails(ArrayList<Email> emails)
    {
        this.emails.setAll(emails);
    }

    public void addUser(User user){
        users.add(user);
    }

    public void addEmail(Email email){
        emails.add(email);
    }
}
