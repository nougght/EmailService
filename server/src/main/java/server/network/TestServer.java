//package server.network;
//
//import java.io.BufferedInputStream;
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//
//public class TestServer {
//    private ServerSocket ssock = null;
////    private Socket sock = null;
//    private DataInputStream in = null;
//
//    public TestServer(int port)
//    {
//        try
//        {
//            ssock = new ServerSocket(port);
//
//            while (true)
//            {
//                System.out.println("Waiting...");
//                var socket = ssock.accept();
//                System.out.print("New Connection: ");
//                System.out.println(socket.getInetAddress());
//                new Thread(new ClientHandler(socket, ))
//
//            }
//
//        }
//        catch(IOException i)
//        {
//            System.out.println(i);
//        }
//    }
//}
