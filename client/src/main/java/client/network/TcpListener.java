package client.network;

import client.dto.EmailDTO;
import client.model.Email;
import client.network.message.Message;
import client.network.message.MessageDeserializer;
import client.network.notification.NewEmailNotification;
import client.network.notification.Notification;
import client.network.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

// прослушивание запросов и ответов от сервера в отдельном потоке
public class TcpListener extends Thread{
    private SSLSocket socket = null;
    private BufferedReader sIn = null;
    private PrintWriter sOut = null;
    final private ObjectMapper jsonMapper = new ObjectMapper();

    final private BlockingQueue<Notification> notifications = new LinkedBlockingQueue<>();
    final private ConcurrentHashMap<UUID, CompletableFuture<Response>> pendingResponses;

    TcpListener(SSLSocket socket, ConcurrentHashMap<UUID, CompletableFuture<Response>> responses)
    {
        super();
        this.socket = socket;
        this.pendingResponses = responses;
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Message.class, new MessageDeserializer());
        jsonMapper.registerModule(module);
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            sIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch(IOException e)
        {
            System.out.println(e.toString());
        }
    }

    public BlockingQueue<Notification> getNotifications() {return notifications;}

    public void run()
    {
        System.out.println("Tcp Listener...");
        try {
            String message = "";
            while(true)
            {
                message = sIn.readLine();

                System.out.println("TCP Listener received message: " + message);

//                JSONObject resp = new JSONObject(jsonResponse);
//                UUID requestId = UUID.fromString(resp.getString("requestId"));
//                String type = resp.getString("type");
                Message msg = jsonMapper.readValue(message, Message.class);

                // если полученное сообщение - ожидаемый ответ на один из запросов
                if (msg.getKind().equals("RESPONSE"))
                {
                    handleResponse((Response) msg);
                }
                // иначе - это команда/сообщение от сервера
                else
                {
                    var ok = notifications.offer((Notification) msg);
                    if (!ok) {
                        System.out.println("Blocking queue error ");
                    }
                }
            }

        } catch (IOException i) {
            System.out.println(i.toString());
        }
    }

    public void handleResponse(Response response)
    {
//        Response response = null;
//        try {
//            switch (type) {
//                case "Registration":
//                    response = jsonMapper.readValue(jsonResponse, RegistrationResponse.class);
//                    break;
//                case "Login":
//                    response = jsonMapper.readValue(jsonResponse, LoginResponse.class);
//                    break;
//                case "Refresh":
//                    response = jsonMapper.readValue(jsonResponse, RefreshResponse.class);
//                    break;
//                case "Logout":
//                    response = jsonMapper.readValue(jsonResponse, LogoutResponse.class);
//                    break;
//                case "GetUser":
//                    response = jsonMapper.readValue(jsonResponse, GetUserResponse.class);
//                    break;
//                case "GetEmails":
//                    response = jsonMapper.readValue(jsonResponse, GetEmailsResponse.class);
//                    break;
//                case "SendEmail":
////                    response = jsonMapper.readValue(jsonResponse, )
//                    break;
//            }
//        }
//        catch(Exception e)
//        {
//            System.out.println(e.toString());
//            throw new RuntimeException(e.toString());
//        }

        if (response == null)
            return;
        var id = response.getRequestId();
        var future = pendingResponses.remove(id);
        System.out.println("response with id " + id + " complete");
        future.complete(response);
    }


    public void newEmailHandler(EmailDTO email)
    {

    }
}
