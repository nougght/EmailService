package client.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import client.dto.EmailDTO;
import client.dto.UserDTO;
import client.mapper.EmailMapper;
import client.mapper.UserMapper;
import client.model.Email;
import client.model.EmailSending;
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

    public Optional<Email> getEmailByEmailId(UUID emailId) {
        Optional<Email> existing = storage.getEmailByEmailId(emailId);
        return existing;
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
        tcpClient.requestAllUserEmails(userId).thenCompose(dtos -> {
            List<UUID> userIds = dtos.stream().flatMap(dto -> {
                return Stream.<UUID>concat(Stream.of(dto.getSenderId()), dto.getRecipientIds().stream());
            }).collect(Collectors.toCollection(ArrayList::new));

            return this.loadUsers(userIds).thenApply(_ -> {
                return dtos.stream().map(this::convert).collect(Collectors.toCollection(ArrayList::new));
            });
        }).thenAccept(emails -> {
            Platform.runLater(() -> storage.setAllEmails(emails));
        });

//                .thenCompose(lst -> {
//                    // to do: ограничить параллельную работу convert из-за возможной перегрузки
//                    var emails = lst.stream().map(e -> convert(e)).collect(Collectors.toCollection(ArrayList::new));
//                    return CompletableFuture.allOf(emails.toArray(new CompletableFuture[0])).
//                            thenApply(
//                                    v -> {
//                                        return emails.stream().map(e -> e.join()).collect(Collectors.toCollection(ArrayList::new));
//
//                                    }
//                            );
//                }
//        )

    }

    public CompletableFuture<Integer> loadUsers(List<UUID> ids) {
        if (ids.isEmpty()) return CompletableFuture.completedFuture(0);
        for (var i = 0; i < ids.size(); ) {
            if (!storage.containsUserWithId(ids.get(i))) {
                ids.remove(i);
            } else {
                i++;
            }
        }
        return tcpClient.requestUsersByIds(ids).thenApply(users -> {
            storage.addUsers(
                    users.values().stream().map(this::convert).collect(Collectors.toMap(User::getUserId, (val) -> val))
            );
            return users.size();
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

    public CompletableFuture<Optional<Email>> sendEmail(EmailSending email) {
        return tcpClient.requestSendEmail(email).thenCompose(optionalDto -> {
            // to do: ограничить параллельную работу convert из-за возможной перегрузки
            if (optionalDto.isEmpty())
                return CompletableFuture.completedFuture(Optional.<Email>empty());
            var dto = optionalDto.get();
            List<UUID> userIds = Stream.<UUID>concat(Stream.of(dto.getSenderId()), dto.getRecipientIds().stream()).collect(Collectors.toCollection(ArrayList::new));
            return loadUsers(userIds).thenApply(_ -> {
                var eml = convert(dto);
                Platform.runLater(() -> storage.addEmail(eml));
                return Optional.of(eml);
            });
        });
    }

    public void addEmail(EmailDTO dto) {
        Platform.runLater(() -> {
            storage.addEmail(convert(dto));
        });

    }

    public User convert(UserDTO dto) {
        var user = UserMapper.fromDTO(dto);
        return user;
    }

    public Email convert(EmailDTO dto) {
        var email = EmailMapper.fromDTO(dto);
        var optionalSender = storage.getUserByUserId(email.getSenderId());
        email.setSender(optionalSender.orElseGet(User::placeholder));

        Optional<User> optionalRecipient;
        List<User> recipients = new ArrayList<>();
        for (var id : dto.getRecipientIds()) {
            optionalRecipient = storage.getUserByUserId(id);
            recipients.add(optionalRecipient.orElseGet(User::placeholder));
        }
        email.setRecipients(recipients);
        // возвращаем полученные данные
        return email;
    }
//        public CompletableFuture<Email> convert (EmailDTO dto){
//            var senderFuture = getUserByUserId(dto.getSenderId());
//            var receiverFuture = getUserByUserId(dto.getReceiverId());
//            // дожидаемся завершения всех асинхронных функций
//            return CompletableFuture.allOf(senderFuture, receiverFuture).thenApply(v -> {
//                var email = EmailMapper.fromDTO(dto);
//                email.setSender(senderFuture.join().get());
//                email.setRecipients(receiverFuture.join().get());
//                // возвращаем полученные данные
//                return email;
//            });
//        }


}
