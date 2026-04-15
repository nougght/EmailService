package server.repositories;

import server.database.DatabaseManager;
import server.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class EmailRepository {
    public ArrayList<Email> getUserEmails(UUID userId) {
        var con = DatabaseManager.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT email_id, sender_id, receiver_id, subject, body, sent_at," +
                    "sender.user_id as sender_id, sender.username as sender_username, " +
                    "sender.email as sender_email," +
                    "sender.created_at as sender_created_at," +
                    "receiver.user_id as receiver_id, receiver.username as receiver_username," +
                    "receiver.email as receiver_email," +
                    "receiver.created_at as receiver_created_at " +
                    "FROM emails " +
                    "LEFT JOIN users as sender ON emails.sender_id = sender.user_id " +
                    "LEFT JOIN users as receiver ON emails.receiver_id = receiver.user_id " +
                    "WHERE emails.sender_id = ? OR emails.receiver_id = ?");
            statement.setObject(1, userId);
            statement.setObject(2, userId);
            var rows = statement.executeQuery();
            System.out.println(rows.toString());
            ArrayList<Email> result = new ArrayList<Email>();
            while (rows.next()) {
                result.add(new Email(
                        UUID.fromString(rows.getString("email_id")),
                        UUID.fromString(rows.getString("sender_id")),
                        UUID.fromString(rows.getString("receiver_id")),
                        rows.getString("subject"),
                        rows.getString("body"),
                        rows.getObject("sent_at", OffsetDateTime.class),
                        new User(
                                rows.getObject("sender_id", UUID.class),
                                rows.getString("sender_username"),
                                null,
                                rows.getString("sender_email"),
                                rows.getObject("sender_created_at", OffsetDateTime.class)
                        ),
                        new User(
                                rows.getObject("receiver_id", UUID.class),
                                rows.getString("receiver_username"),
                                null,
                                rows.getString("receiver_email"),
                                rows.getObject("receiver_created_at", OffsetDateTime.class)
                        )
                ));
            }

            return result;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return new ArrayList<>();
        }
    }

    public Optional<UUID> addEmail(Email email) {
        var con = DatabaseManager.getConnection();
        try {
            PreparedStatement st = con.prepareStatement("INSERT INTO emails (sender_id, receiver_id, subject, body) " +
                    "VALUES (?, ?, ?, ?) RETURNING email_id");

            st.setObject(1, email.getSenderId());
            st.setObject(2, email.getReceiverId());
            st.setObject(3, email.getSubject());
            st.setObject(4, email.getBody());

            var rows = st.executeQuery();
            if (rows.next()) {
                return Optional.of( rows.getObject("email_id", UUID.class));
            }

        } catch (SQLException e) {
            System.out.println("SQLException " + e.toString());

        }
        return Optional.<UUID>empty();
    }


    public Optional<Email> getEmail(UUID emailId) {
        try {
            var con = DatabaseManager.getConnection();

            var st = con.prepareStatement("SELECT email_id, sender_id, receiver_id, subject, body, sent_at," +
                    "sender.user_id as sender_id, sender.username as sender_username, " +
                    "sender.email as sender_email," +
                    "sender.created_at as sender_created_at," +
                    "receiver.user_id as receiver_id, receiver.username as receiver_username," +
                    "receiver.email as receiver_email," +
                    "receiver.created_at as receiver_created_at " +
                    "FROM emails " +
                    "LEFT JOIN users as sender ON emails.sender_id = sender.user_id " +
                    "LEFT JOIN users as receiver ON emails.receiver_id = receiver.user_id " +
                    "WHERE emails.email_id = ?");
            st.setObject(1, emailId);

            var rows = st.executeQuery();
            if (rows.next()) {
                return Optional.of(new Email(
                        UUID.fromString(rows.getString("email_id")),
                        UUID.fromString(rows.getString("sender_id")),
                        UUID.fromString(rows.getString("receiver_id")),
                        rows.getString("subject"),
                        rows.getString("body"),
                        rows.getObject("sent_at", OffsetDateTime.class),
                        new User(
                                rows.getObject("sender_id", UUID.class),
                                rows.getString("sender_username"),
                                null,
                                rows.getString("sender_email"),
                                rows.getObject("sender_created_at", OffsetDateTime.class)
                        ),
                        new User(
                                rows.getObject("receiver_id", UUID.class),
                                rows.getString("receiver_username"),
                                null,
                                rows.getString("receiver_email"),
                                rows.getObject("receiver_created_at", OffsetDateTime.class)
                        )
                ));
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return Optional.<Email>empty();
    }
}
