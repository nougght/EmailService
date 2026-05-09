package server.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import server.database.DatabaseManager;
import server.model.User;

public class UserRepository {

    public boolean checkUserExisting(String username)
    {
        var con = DatabaseManager.getConnection();
        try {
            PreparedStatement st = con.prepareStatement("SELECT * FROM users WHERE users.username = ?");
            st.setString(1, username);

            var rows = st.executeQuery();
            if (rows.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("SQLException " + e.toString());
        }
        return false;
    }

    public Optional<User> getUserByUsername(String username) {
        var con = DatabaseManager.getConnection();
        try {
            PreparedStatement st = con.prepareStatement("SELECT * FROM users WHERE users.username = ?");
            st.setString(1, username);

            var rows = st.executeQuery();
            if (rows.next()) {
                User result = new User(
                        UUID.fromString(rows.getString("user_id")),
                        rows.getString("username"),
                        rows.getString("password_hash"),
                        rows.getString("email"),
                        rows.getObject("created_at", OffsetDateTime.class)
                );
                return Optional.of(result);
            }
        } catch (SQLException e) {
            System.out.println("SQLException " + e.toString());
        }
        return Optional.empty();
    }

    public Optional<User> getUserById(UUID userId) {
        var con = DatabaseManager.getConnection();
        try {
            PreparedStatement st = con.prepareStatement("SELECT * FROM users" +
                    " WHERE users.user_id = ?"
            );

            st.setObject(1, userId);

            var rows = st.executeQuery();
            if (rows.next()) {
                User result = new User(
                        UUID.fromString(rows.getString("user_id")),
                        rows.getString("username"),
                        rows.getString("password_hash"),
                        rows.getString("email"),
                        rows.getObject("created_at", OffsetDateTime.class)
                );
                return Optional.of(result);
            }
        } catch (SQLException e) {
            System.out.println("SQLException " + e.toString());
        }
        return Optional.empty();
    }

    public Map<UUID, User> getUsersByIds(List<UUID> userIds) {
        try {
            var con = DatabaseManager.getConnection();

            var st = con.prepareStatement("SELECT * FROM users WHERE users.user_id = ANY(?)");
            st.setArray(1, con.createArrayOf("UUID", userIds.toArray()));

            var rows = st.executeQuery();

            Map<UUID, User> result = new HashMap<>();
            while (rows.next()) {
                var userId = rows.getObject("user_id", UUID.class);
                result.put(userId, new User(
                        userId,
                        rows.getString("username"),
                        rows.getString("password_hash"),
                        rows.getString("email"),
                        rows.getObject("created_at", OffsetDateTime.class)
                ));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, User> getUsersByUsernames(List<String> usernames) {
        try {
            var con = DatabaseManager.getConnection();

            var st = con.prepareStatement("SELECT * FROM users WHERE users.username = ANY(?)");
            st.setArray(1, con.createArrayOf("VARCHAR", usernames.toArray()));

            var rows = st.executeQuery();

            Map<String, User> result = new HashMap<>();
            while (rows.next()) {
                var username = rows.getString("username");
                result.put(username, new User(
                        rows.getObject("user_id", UUID.class),
                        username,
                        rows.getString("password_hash"),
                        rows.getString("email"),
                        rows.getObject("created_at", OffsetDateTime.class)
                ));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser(User user) {
        var con = DatabaseManager.getConnection();
        try {
            PreparedStatement st = con.prepareStatement("INSERT INTO users (username, password_hash, email) " +
                    "VALUES (?, ?, ?)"
            );

            st.setObject(1, user.getUsername());
            st.setObject(2, user.getPasswordHash());
            st.setObject(3, user.getEmail());

            st.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQLException " + e.toString());
        }
    }

}
