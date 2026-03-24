package server.network;

import server.services.EmailService;
import server.services.UserService;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

public class TcpServer {

    private ServerSocket ssock = null;
    //    private Socket sock = null;
    private DataInputStream in = null;

    final private EmailService emailService;
    final private UserService userService;

    public TcpServer(int port, EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;

        try {
            ssock = new ServerSocket(port);

            while (true) {
                System.out.println("Waiting...");
                var socket = ssock.accept();
                System.out.print("New Connection: ");
                System.out.println(socket.getInetAddress());
                new Thread(new ClientHandler(socket, emailService, userService)).start();

            }

        } catch (IOException i) {
            System.out.println("tcp server: " + i);
        }
    }
}


