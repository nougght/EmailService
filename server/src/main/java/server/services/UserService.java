package server.services;

import io.jsonwebtoken.security.Jwks;
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
        if (userId == null)
            return Optional.<User>empty();
        var existing = userRepo.getUserById(userId);
        return existing;
    }

    public Optional<User> getUserByUsername(String username) {
        var existing = userRepo.getUserByUsername(username);
        return  existing;
    }

    public void addUser(User user)
    {
        userRepo.addUser(user);
    }

}
