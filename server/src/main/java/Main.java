
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import io.github.cdimascio.dotenv.Dotenv;
import server.database.DatabaseManager;
import server.network.TcpServer;
import server.repositories.DraftRepository;
import server.repositories.EmailRepository;
import server.repositories.TokenRepository;
import server.repositories.UserRepository;
import server.services.AuthService;
import server.services.DraftService;
import server.services.EmailService;
import server.services.UserService;

public class Main {
    public static void main() throws Exception {
        // TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the
        // highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
//        Class.forName("org.postgresql.Driver");

        Dotenv dotenv = Dotenv.load();
        System.out.println(dotenv.get("DB_URL"));
        final DatabaseManager dm = new DatabaseManager(dotenv.get("DB_URL"), dotenv.get("DB_USER"), dotenv.get("DB_PASSWORD"));
        EmailRepository emailRepo = new EmailRepository();
        UserRepository userRepo = new UserRepository();
        TokenRepository tokenRepo = new TokenRepository();
        DraftRepository draftRepo = new DraftRepository();
        AuthService authService = new AuthService(userRepo, tokenRepo, dotenv.get("JWT_SECRET_KEY"));
        DraftService draftService = new DraftService(draftRepo);
        EmailService emailService = new EmailService(emailRepo, draftService);
        UserService userService = new UserService(userRepo);

        new TcpServer(3741, authService, draftService, emailService, userService);
    }
}

