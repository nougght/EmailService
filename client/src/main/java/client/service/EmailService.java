package client.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import client.dto.EmailDTO;
import client.dto.UserDTO;
import client.mapper.EmailMapper;
import client.mapper.UserMapper;
import client.model.Email;
import client.model.User;
import client.network.TcpClient;
import client.storage.DataStorage;
import javafx.application.Platform;
import javafx.collections.ObservableList;

public class EmailService {
    final private TcpClient tcpClient;
    final private SessionService sessionService;
    final private DataStorage storage;

    public EmailService(TcpClient tcpClient, SessionService sessionService, DataStorage storage) {
        this.tcpClient = tcpClient;
        this.sessionService = sessionService;
        this.storage = storage;
        

        sessionService.addListener(s -> {
            //temp: нужно исключить возможность гонки данных, реализовать возможность отмены задачи
            loadUserEmails(sessionService.getCurrentUser().getValue().getUserId());
        });

    }

    public CompletableFuture<Optional<User>> getUserByUserId(UUID userId) {
        // проверка наличия в хранилище
        Optional<User> existing = storage.getUserByUserId(userId);

        // если в храналище нет, подгружаем с сервера
        if (existing.isEmpty()) {
            Optional<UserDTO> userDTO;
            return tcpClient.requestUserByUserIdAsync(userId).thenApply(u -> {
                if (u.isPresent()) {
                    var user = convert(u.get());
                    storage.addUser(user);
                    return Optional.of(user);
                } else {
                    throw new RuntimeException("user with that id doesn't exist");
                }
            });

        }
        // если элемет найден в хранилище - возвращаем готовый результат
        return CompletableFuture.completedFuture(existing);

    }

    public ObservableList<Email> getEmailsList() {
        return storage.getAllEmails();
    }

    public void loadUserEmails(UUID userId) {
        if (userId == null) {
            var user = sessionService.getCurrentUser().getValue();
            if (user == null)
                return;
            userId = user.getUserId();
        }
        tcpClient.requestAllUserEmails(userId).thenCompose(lst -> {
                    // to do: ограничить параллельную работу convert из-за возможной перегрузки
                    var emails = lst.stream().map(e -> convert(e)).collect(Collectors.toCollection(ArrayList::new));
                    return CompletableFuture.allOf(emails.toArray(new CompletableFuture[0])).
                            thenApply(
                                    v -> {
                                        return emails.stream().map(e -> e.join()).collect(Collectors.toCollection(ArrayList::new));

                                    }
                            );
                }
        ).thenAccept(emails -> {
            Platform.runLater(() -> storage.setAllEmails(emails));
        });
    }

//    public CompletableFuture<ArrayList<Email>> loadUserEmails(UUID userId) {
//
//        return tcpClient.requestAllUserEmails(userId).thenCompose(lst -> {
//                    // to do: ограничить параллельную работу convert из-за возможной перегрузки
//                    var emails = lst.stream().map(e -> convert(e)).collect(Collectors.toCollection(ArrayList::new));
//                    return CompletableFuture.allOf(emails.toArray(new CompletableFuture[0])).thenApply(
//                            v -> {
//                                return emails.stream().map(e -> e.join()).collect(Collectors.toCollection(ArrayList::new));
//                            }
//                    );
//                }
//        );
//    }

    public User convert(UserDTO dto) {
        var user = UserMapper.fromDTO(dto);
        return user;
    }

    public CompletableFuture<Email> convert(EmailDTO dto) {
        var senderFuture = getUserByUserId(dto.getSenderId());
        var receiverFuture = getUserByUserId(dto.getReceiverId());
        // дожидаемся завершения всех асинхронных функций
        return CompletableFuture.allOf(senderFuture, receiverFuture).thenApply(v -> {
            var email = EmailMapper.fromDTO(dto);
            email.setSender(senderFuture.join().get());
            email.setReceiver(receiverFuture.join().get());
            // возвращаем полученные данные
            return email;
        });
    }


}
