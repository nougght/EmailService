package server.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import server.database.DatabaseManager;
import server.model.User;

import javax.xml.crypto.Data;

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
