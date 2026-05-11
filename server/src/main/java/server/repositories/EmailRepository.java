package server.repositories;

import server.database.DatabaseManager;
import server.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;

public class EmailRepository {
    public ArrayList<Email> getUserEmails(UUID userId) {
        var con = DatabaseManager.getConnection();
        try {

            PreparedStatement statement = con.prepareStatement("""
                    SELECT ue.email_id, ue.is_read, ue.folder, sender_id, subject, body, sent_at,
                    sender.username as sender_username,
                    r.recipient_ids,
                    r.recipient_usernames
                    FROM user_emails as ue
                    LEFT JOIN emails on emails.email_id = ue.email_id
                    LEFT JOIN users as sender ON emails.sender_id = sender.user_id
                    LEFT JOIN (
                        SELECT
                            email_id,
                            array_agg(user_id) as recipient_ids,
                            array_agg(username) as recipient_usernames
                        FROM email_recipients
                        GROUP BY email_id
                        ) r ON emails.email_id = r.email_id
                    WHERE ue.user_id = ?
                    """);
            statement.setObject(1, userId);
//            statement.setObject(2, userId);
            var rows = statement.executeQuery();
            System.out.println(rows.toString());
            ArrayList<Email> result = new ArrayList<Email>();
            while (rows.next()) {
                var email = new Email(
                        UUID.fromString(rows.getString("email_id")),
                        UUID.fromString(rows.getString("sender_id")),
                        rows.getString("sender_username"),
                        rows.getString("subject"),
                        rows.getString("body"),
                        rows.getObject("sent_at", OffsetDateTime.class),
                        new User(
                                rows.getObject("sender_id", UUID.class),
                                rows.getString("sender_username"),
                                null,
                                null,
                                null
//                                rows.getString("sender_email"),
//                                rows.getObject("sender_created_at", OffsetDateTime.class)
                        ),
                        null,
                        null
                );
                PreparedStatement detailsQuery = con.prepareStatement("""
                        SELECT folder, is_read FROM user_emails
                        WHERE email_id = ? AND user_id = ?
                        """);
                detailsQuery.setObject(1, email.getEmailId());
                detailsQuery.setObject(2, userId);
                var detailsRows = detailsQuery.executeQuery();
                if (detailsRows.next()) {
                    email.setDetails(new UserEmail(
                            userId,
                            detailsRows.getString("folder"),
                            detailsRows.getBoolean("is_read")));
                }

                var recipientIds = (UUID[]) rows.getArray("recipient_ids").getArray();
                var recipientUsernames = (String[]) rows.getArray("recipient_usernames").getArray();
                var recipients = new ArrayList<EmailRecipient>(recipientIds.length);

                for (int i = 0; i < recipientIds.length; i++) {
                    var r = new EmailRecipient(email.getEmailId(), recipientIds[i], recipientUsernames[i]);
                    recipients.add(r);
                }
                email.setRecipients(recipients);
                result.add(email);
            }

            return result;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return new ArrayList<>();
        }
    }

    public Optional<UUID> addEmail(Email email) {
        try (var con = DatabaseManager.getConnection()) {
            con.setAutoCommit(false);
            try {
                if (email.getSenderUsername() == null) {
                    PreparedStatement st = con.prepareStatement("SELECT username FROM users WHERE users.user_id = ?");
                    st.setObject(1, email.getSenderId().orElseThrow());
                    var rows = st.executeQuery();
                    if (rows.next()) {
                        email.setSenderUsername(rows.getString("username"));
                    }
                }
                PreparedStatement st = con.prepareStatement("INSERT INTO emails (sender_id, sender_username, subject, body) " +
                        "VALUES (?, ?, ?, ?) RETURNING email_id");

                st.setObject(1, email.getSenderId().orElseThrow());
                st.setString(2, email.getSenderUsername());
                st.setObject(3, email.getSubject());
                st.setObject(4, email.getBody());

                var rows = st.executeQuery();

                UUID email_id = null;
                if (rows.next()) {
                    email_id = rows.getObject("email_id", UUID.class);

                    PreparedStatement idsQuery = con.prepareStatement("""
                            SELECT user_id FROM users
                            WHERE users.username = ANY(?)
                            """);
                    idsQuery.setArray(1, con.createArrayOf("varchar", email.getRecipients().stream().map(EmailRecipient::getUsername).toArray()));
                    rows = idsQuery.executeQuery();
                    var ids = new ArrayList<UUID>();
                    while (rows.next()) {
                        ids.add(rows.getObject("user_id", UUID.class));
                    }

                    // добавляем получателей в бд
                    PreparedStatement insRecipients = con.prepareStatement("""
                            INSERT INTO email_recipients(email_id, username, user_id)
                            VALUES (?, ?, ?)
                            """);
                    //                            SELECT ?, ?, user_id
                    //                            FROM users
                    //                            WHERE users.username = ?
                    //                            """);

                    for (int i = 0; i < email.getRecipients().size(); i++) {
                        var username = email.getRecipients().get(i).getUsername();
                        insRecipients.setObject(1, email_id);
                        insRecipients.setString(2, username);
                        insRecipients.setObject(3, ids.get(i));
                        insRecipients.addBatch();
                    }
                    insRecipients.executeBatch();

                    // добавляем письмо в дополнительную таблицу
                    PreparedStatement insUserEmails = con.prepareStatement("""
                            INSERT INTO user_emails(user_id, email_id, folder, is_read)
                            VALUES (?, ?, ?, ?)
                            """);
                    // для отправителя
                    insUserEmails.setObject(1, email.getSenderId().orElseThrow());
                    insUserEmails.setObject(2, email_id);
                    insUserEmails.setString(3, "OUTBOX");
                    insUserEmails.setBoolean(4, true);
                    insUserEmails.addBatch();

                    for (var i = 0; i < email.getRecipients().size(); i++) {
                        insUserEmails.setObject(1, ids.get(i));
                        insUserEmails.setObject(2, email_id);
                        insUserEmails.setString(3, "INBOX");
                        insUserEmails.setBoolean(4, false);
                        insUserEmails.addBatch();
                    }
                    var lst = insUserEmails.executeBatch();

                    con.commit();
                    return Optional.of(email_id);
                }


            } catch (SQLException e) {
                con.rollback();
                System.out.println("SQLException " + e.toString());
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.<UUID>empty();
    }

    public Optional<Email> getEmail(UUID emailId, UUID userId) {
        var con = DatabaseManager.getConnection();
        try {
            con.setAutoCommit(false);
//            var st = con.prepareStatement("""
//                    SELECT emails.email_id, sender_id, subject, body, sent_at,
//                    sender.username as sender_username,
//                    r.recipient_ids,
//                    r.recipient_usernames
//                    FROM emails
//                    LEFT JOIN users as sender ON emails.sender_id = sender.user_id
//                    LEFT JOIN (
//                        SELECT
//                            email_id,
//                            array_agg(user_id) as recipient_ids,
//                            array_agg(username) as recipient_usernames
//                        FROM email_recipients
//                        GROUP BY email_id
//                        ) r ON emails.email_id = r.email_id
//                    WHERE emails.email_id = ?
//                    """);
//            st.setObject(1, emailId);
//
//            var rows = st.executeQuery();
//            if (rows.next()) {
//                var email = new Email(
//                        UUID.fromString(rows.getString("email_id")),
//                        UUID.fromString(rows.getString("sender_id")),
//                        rows.getString("sender_username"),
//                        rows.getString("subject"),
//                        rows.getString("body"),
//                        rows.getObject("sent_at", OffsetDateTime.class),
//                        new User(
//                                rows.getObject("sender_id", UUID.class),
//                                rows.getString("sender_username"),
//                                null,
//                                null,
//                                null
////                                rows.getString("sender_email"),
////                                rows.getObject("sender_created_at", OffsetDateTime.class)
//                        ),
//                        null,
//                        null
//
//                );
//                if (userId != null) {
//                    PreparedStatement detailsQuery = con.prepareStatement("""
//                            SELECT folder, is_read FROM user_emails
//                            WHERE email_id = ? AND user_id = ?
//                            """);
//                    detailsQuery.setObject(1, emailId);
//                    detailsQuery.setObject(2, userId);
//                    var detailsRows = detailsQuery.executeQuery();
//                    if (detailsRows.next()) {
//                        email.setDetails(new UserEmail(
//                                userId,
//                                detailsRows.getString("folder"),
//                                detailsRows.getBoolean("is_read")));
//                    }
//                }
//                var recipientIds = (UUID[]) rows.getArray("recipient_ids").getArray();
//                var recipientUsernames = (String[]) rows.getArray("recipient_usernames").getArray();
//                var recipients = new ArrayList<EmailRecipient>(recipientIds.length);
//
//                for (int i = 0; i < recipientIds.length; i++) {
//                    var r = new EmailRecipient(emailId, recipientIds[i], recipientUsernames[i]);
//                    recipients.add(r);
//                }
//                email.setRecipients(recipients);

            var st = con.prepareStatement("""
                    SELECT ue.email_id, ue.is_read, ue.folder, sender_id, subject, body, sent_at,
                    sender.username as sender_username,
                    r.recipient_ids,
                    r.recipient_usernames
                    FROM user_emails as ue
                    LEFT JOIN emails on emails.email_id = ue.email_id
                    LEFT JOIN users as sender ON emails.sender_id = sender.user_id
                    LEFT JOIN (
                        SELECT
                            email_id,
                            array_agg(user_id) as recipient_ids,
                            array_agg(username) as recipient_usernames
                        FROM email_recipients
                        GROUP BY email_id
                        ) r ON emails.email_id = r.email_id
                    WHERE ue.email_id = ? AND ue.user_id = ?
                    """);
            st.setObject(1, emailId);
            st.setObject(2, userId);

            var rows = st.executeQuery();
            if (rows.next()) {
                var email = new Email(
                        UUID.fromString(rows.getString("email_id")),
                        UUID.fromString(rows.getString("sender_id")),
                        rows.getString("sender_username"),
                        rows.getString("subject"),
                        rows.getString("body"),
                        rows.getObject("sent_at", OffsetDateTime.class),
                        new User(
                                rows.getObject("sender_id", UUID.class),
                                rows.getString("sender_username"),
                                null,
                                null,
                                null
//                                rows.getString("sender_email"),
//                                rows.getObject("sender_created_at", OffsetDateTime.class)
                        ),
                        null,
                        new UserEmail(
                                userId,
                                rows.getString("folder"),
                                rows.getBoolean("is_read")
                        )

                );

                var recipientIds = (UUID[]) rows.getArray("recipient_ids").getArray();
                var recipientUsernames = (String[]) rows.getArray("recipient_usernames").getArray();
                var recipients = new ArrayList<EmailRecipient>(recipientIds.length);

                for (int i = 0; i < recipientIds.length; i++) {
                    var r = new EmailRecipient(emailId, recipientIds[i], recipientUsernames[i]);
                    recipients.add(r);
                }
                email.setRecipients(recipients);
                con.commit();
                return Optional.of(email);
            }

        } catch (Exception e) {
            System.out.println(e.toString());
            try {
                con.rollback();
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
        return Optional.<Email>empty();
    }

    public void deleteUserEmail(UUID userId, UUID emailId) {
        var con = DatabaseManager.getConnection();
        try {
            var st = con.prepareStatement("""
                    DELETE FROM user_emails WHERE user_id = ? AND email_id = ?
                    """);
            st.setObject(1, userId);
            st.setObject(2, emailId);

            st.executeUpdate();

        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }
}
