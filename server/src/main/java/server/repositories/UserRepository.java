package server.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import server.database.DatabaseManager;
import server.model.User;

public class UserRepository {
    public Optional<User> getUserById(UUID userId)
    {
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
                        rows.getString("email"),
                        rows.getObject("created_at", OffsetDateTime.class)
                );
                return Optional.of(result);
            }
        } catch(SQLException e)
        {
            System.out.println("SQLException " + e.toString());
        }
        return Optional.empty();
    }
    
}
