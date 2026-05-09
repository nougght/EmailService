package server.repositories;

import common.dto.Draft;
import server.database.DatabaseManager;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DraftRepository {

    public Optional<UUID> addDraft(Draft draft) {
        var con = DatabaseManager.getConnection();
        try {
            var st = con.prepareStatement("""
                INSERT INTO drafts(sender_id, subject, body, recipients)
                VALUES (?, ?, ?, ?)
                RETURNING draft_id
                """);
            st.setObject(1, draft.getSenderId());
            st.setObject(2, draft.getSubject());
            st.setObject(3, draft.getBody());
            st.setArray(4, con.createArrayOf("text", draft.getRecipients().toArray()));

            var rows = st.executeQuery();
            if (rows.next()) {
                return Optional.of(rows.getObject("draft_id", UUID.class));
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return Optional.<UUID>empty();
    }

    public List<Draft> getDraftsByUserId(UUID userId) {
        var con = DatabaseManager.getConnection();
        try {
            var st = con.prepareStatement("""
                    SELECT * FROM drafts
                    WHERE drafts.sender_id = ?
                    """);
            st.setObject(1, userId);
            var rows = st.executeQuery();
            var result = new ArrayList<Draft>();
            while (rows.next()) {
                result.add(new Draft(
                        rows.getObject("draft_id", UUID.class),
                        rows.getObject("sender_id", UUID.class),
                        new ArrayList<String> (List.of((String[]) rows.getArray("recipients").getArray())),
                        rows.getString("subject"),
                        rows.getString("body"),
                        rows.getObject("updated_at", OffsetDateTime.class),
                        rows.getObject("created_at", OffsetDateTime.class)
                ));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean draftExists(UUID draftId) {
        var con = DatabaseManager.getConnection();
        try {
            var st = con.prepareStatement("""
                    SELECT 1 FROM drafts WHERE drafts.draft_id = ?
                    """);
            st.setObject(1, draftId);
            var rows = st.executeQuery();
            if (rows.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateDraft(Draft draft) {
        var con = DatabaseManager.getConnection();
        try {
            var st = con.prepareStatement("""
                    UPDATE drafts
                    SET recipients = ?, subject = ?, body = ?
                    WHERE drafts.draft_id = ?
                    """);
            st.setArray(1, con.createArrayOf("varchar", draft.getRecipients().toArray()));
            st.setString(2, draft.getSubject());
            st.setString(3, draft.getBody());
            st.setObject(4, draft.getDraftId());

            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(UUID draftId) {
        var con = DatabaseManager.getConnection();
        try {
            var st = con.prepareStatement("""
                
                    DELETE FROM drafts WHERE drafts.draft_id = ?;
                """);
            st.setObject(1, draftId);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
