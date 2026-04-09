package server.repositories;

import server.database.DatabaseManager;
import server.model.Token;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class TokenRepository {

    public Optional<Token> getRefreshToken(UUID userId) {
        try {
            var con = DatabaseManager.getConnection();

            PreparedStatement st = con.prepareStatement(
                    "SELECT * FROM tokens WHERE user_id = ? ORDER BY tokens.expires_at DESC"
            );
            st.setObject(1, userId);

            var rows = st.executeQuery();
            if (rows.next()) {
                return Optional.of(new Token(
                        rows.getObject("token_id", UUID.class),
                        rows.getObject("user_id", UUID.class),
                        rows.getString("token_hash"),
                        rows.getObject("created_at", OffsetDateTime.class),
                        rows.getObject("expires_at", OffsetDateTime.class)
                ));
            } else {
                return Optional.<Token>empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addRefreshToken(Token token)
    {
        try {
            var con = DatabaseManager.getConnection();

            var st = con.prepareStatement("DELETE FROM tokens WHERE user_id = ?");
            st.setObject(1, token.getUserId());
            st.executeUpdate();


            st = con.prepareStatement(
                    "INSERT INTO tokens(user_id, token_hash, expires_at) VALUES (?, ?, ?)"
            );
            st.setObject(1, token.getUserId());
            st.setString(2, token.getTokenHash());
            st.setObject(3, token.getExpiresAt());

            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRefreshTokens(UUID userId)
    {
        var con = DatabaseManager.getConnection();
        try {
            var st = con.prepareStatement("DELETE FROM tokens WHERE tokens.user_id = ?");
            st.setObject(1, userId);
            st.executeUpdate();
        } catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
