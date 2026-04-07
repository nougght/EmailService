package server.network;

import server.services.AuthService;
import server.services.EmailService;
import server.services.UserService;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;

public class TcpServer {

    private SSLServerSocket ssock = null;
    //    private Socket sock = null;
    private DataInputStream in = null;

    final private AuthService authService;
    final private EmailService emailService;
    final private UserService userService;

    public TcpServer(int port, AuthService authService, EmailService emailService, UserService userService) {
        this.authService = authService;
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
                new Thread(new ClientHandler(socket, authService, emailService, userService)).start();

            }

        } catch (Exception i) {
            System.out.println("tcp server: " + i);
        }
    }
}


