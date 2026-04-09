package server.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.javatuples.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import server.model.Token;
import server.model.User;
import server.repositories.TokenRepository;
import server.repositories.UserRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;


public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    ;
    private final String secretKey;
    private final SecretKey jwtSecretKey;

    public AuthService(UserRepository userRepository, TokenRepository tokenRepository, String secretKey) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;

        this.secretKey = secretKey;
        jwtSecretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
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

        if (checkPassword(password, user.getPasswordHash())) {
            return new Pair<User, String>(user, "success");
        }
        return new Pair<User, String>(null, "incorrect password");

    }

    public String genAccessToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 80_000_000))
                .signWith(jwtSecretKey)
                .compact();
    }

    public UUID verifyAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID userId = UUID.fromString(claims.getSubject());
            return userId;
        } catch (JwtException e) {
            System.out.println("Failed jwt verification");
            return null;
        }
    }

    public static String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkRefreshToken(String token, UUID userId) {
        try {
            var latestToken = tokenRepository.getRefreshToken(userId);
            // токен должен совпадать с сохраненным и не истекшим
            var hash = sha256(token);
            System.out.println("incoming token: " + token);
            System.out.println("incoming hash:  " + hash);
            System.out.println("stored hash:    " + latestToken.get().getTokenHash());
            return !(latestToken.isEmpty() || !hash.equals(latestToken.get().getTokenHash())
                    || latestToken.get().getExpiresAt().isBefore(OffsetDateTime.now()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void addRefreshToken(String rawToken, UUID userId) {
        try {
            System.out.println("raw token hashed " + rawToken);
            Token token = new Token(
                    null,
                    userId,
                    sha256(rawToken),
                    null,
                    OffsetDateTime.now().plusDays(7)
            );

            tokenRepository.addRefreshToken(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
