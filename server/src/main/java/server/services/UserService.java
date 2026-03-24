package server.services;

import server.model.User;
import server.repositories.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class UserService {
    final private UserRepository userRepo;
    public UserService(UserRepository userRepo)
    {
        this.userRepo = userRepo;
    }

    public Optional<User> getUserByUserId(UUID userId)
    {
        var existing = userRepo.getUserById(userId);
        return existing;
    }

}
