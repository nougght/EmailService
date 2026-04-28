package client.storage;

import client.model.Email;
import client.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

// хранилище, предоставляющее доступ к данным, но без логики обработки
public class DataStorage {
    final private ObservableList<Email> emails = FXCollections.observableArrayList();
    final private Map<UUID, User> users = new HashMap<>();

//      emails

    public Optional<Email> getEmailByEmailId(UUID emailId) {
        return emails.stream().filter(e -> e.getEmailId() == emailId).findFirst();
    }

    public ArrayList<Email> getEmailsBySenderId(UUID senderId) {
        return emails.stream().filter(e -> e.getSenderId() == senderId).
                collect(Collectors.toCollection(ArrayList::new));
    }

    public ObservableList<Email> getAllEmails() {
        return emails;
    }

//    public ArrayList<Email> getEmailsByReceiverId(UUID receiverId) {
//        return emails.stream().filter(e -> e.getReceiverId() == receiverId).
//                collect(Collectors.toCollection(ArrayList::new));
//    }

    public void setAllEmails(ArrayList<Email> emails) {
        this.emails.setAll(emails);
    }

//    users

    public Optional<User> getUserByUserId(UUID userId) {
        return Optional.ofNullable(users.get(userId));
    }

//    public Optional<User> getUserByUsername(String username)
//    {
//        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
//    }


    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }


    public void addUsers(Map<UUID, User> users){
        this.users.putAll(users);
    }

    public boolean containsUserWithId(UUID id){
        return users.containsKey(id);
    }


    public void addEmail(Email email) {
        emails.add(email);
    }
}
