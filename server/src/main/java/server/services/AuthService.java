package server.services;

import org.javatuples.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import server.model.User;
import server.repositories.UserRepository;


public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean checkPassword(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }


    public Pair<User, String> register(String username, String password) {
        if (userRepository.checkUserExisting(username))
            return new Pair<User, String>(null, "duplicate");

        var user = new User(null, username, encodePassword(password), null, null);
        userRepository.addUser(user);
        return new Pair<User, String>(user, "success");


    }

    public Pair<User, String> login(String username, String password) {
        var existing = userRepository.getUserByUsername(username);

        if (existing.isEmpty())
            return new Pair<User, String>(null, "not found");
        var user = existing.get();

        if (checkPassword(password, user.getPasswordHash()))
        {
            return new Pair<User, String>(user, "success");
        }
        return new Pair<User, String>(null, "incorrect password");

    }

}
