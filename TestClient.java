import java.io.*;
import java.net.*;

public class TestClient {

    private Socket sock = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public TestClient(String host, int port) {
        try {
            System.out.println("Connecting...");
            sock = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
            System.out.print("Connected: ");
            System.out.println(sock.getInetAddress());

        } catch (UnknownHostException u) {
            System.out.println(u);
            return;
        } catch (IOException i) {
            System.out.println(i);
            return;
        }

            String m = "{'type': 'GetEmails', 'user_id': '4c9eecba-eda1-4e6b-a550-6aa2b7794e19'}\\n";
            out.println(m);
            System.out.println("Message sent");
            
            try {
                System.out.println(in.readLine());
                System.out.println("Message received");
            // out.flush();
            // out.flush();
                // Thread.currentThread().sleep(1000000);
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            } catch (IOException i) {
                System.out.println(i);
            }

        // Close the connection
        try {
            in.close();
            out.close();
            sock.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        TestClient c = new TestClient("127.0.0.1", 3741);
    }
}