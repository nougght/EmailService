package server.services;

import io.jsonwebtoken.security.Jwks;
import server.model.User;
import server.repositories.UserRepository;

import java.util.List;
import java.util.Map;
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

    public Map<UUID, User> getUsersByIds(List<UUID> userIds){
        return userRepo.getUsersByIds(userIds);
    }

    public Map<String, User> getUsersByUsernames(List<String> usernames) {
        return userRepo.getUsersByUsernames(usernames);
    }

    public void addUser(User user)
    {
        userRepo.addUser(user);
    }

}
