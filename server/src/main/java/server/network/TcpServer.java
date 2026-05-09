package server.network;

import java.io.DataInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import common.dto.Draft;
import server.services.AuthService;
import server.services.DraftService;
import server.services.EmailService;
import server.services.UserService;

public class TcpServer {

    private SSLServerSocket ssock = null;
    //    private Socket sock = null;
    private DataInputStream in = null;

    private final AuthService authService;
    private final DraftService draftService;
    private final EmailService emailService;
    private final UserService userService;
    private final ConnectionManager connectionManager = new ConnectionManager();

    public TcpServer(int port, AuthService authService, DraftService draftService, EmailService emailService, UserService userService) {
        this.authService = authService;
        this.draftService = draftService;
        this.emailService = emailService;
        this.userService = userService;

        try {

            // настройка SSLServerSocket для шифрования
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(getClass().getClassLoader()
                    .getResourceAsStream("serverkeystore.jks"), "49Kst0rE/701".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, "49Kst0rE/701".toCharArray());

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), null, null);

            SSLServerSocketFactory ssf = ctx.getServerSocketFactory();

            ssock = (SSLServerSocket) ssf.createServerSocket(port);
            ssock.setNeedClientAuth(false);
            ssock.setWantClientAuth(false);

            while (true) {
                System.out.println("Waiting...");
                var socket = ssock.accept();
                System.out.print("New Connection: ");
                System.out.println(socket.getInetAddress());
                new Thread(new ClientHandler(socket, authService, draftService, emailService, userService, connectionManager)).start();

            }

        } catch (Exception i) {
            System.out.println("tcp server: " + i);
        }
    }
}

