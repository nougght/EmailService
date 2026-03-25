package client.network;

import client.dto.EmailDTO;
import client.network.response.GetEmailsResponse;
import client.network.response.GetUserResponse;
import client.network.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

// прослушивание запросов и ответов от сервера в отдельном потоке
public class TcpListener extends Thread{
    private SSLSocket socket = null;
    private BufferedReader sIn = null;
    private PrintWriter sOut = null;
    final private ObjectMapper jsonMapper = new ObjectMapper();

    final private ConcurrentHashMap<UUID, CompletableFuture<Response>> pendingResponses;

    TcpListener(SSLSocket socket, ConcurrentHashMap<UUID, CompletableFuture<Response>> responses)
    {
        super();
        this.socket = socket;
        this.pendingResponses = responses;
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

    public void run()
    {
        System.out.println("Tcp Listener...");
        try {
            String jsonResponse = "";
            while(true)
            {
                jsonResponse = sIn.readLine();

                System.out.println("TCP Listener received message: " + jsonResponse);

                JSONObject resp = new JSONObject(jsonResponse);
                UUID requestId = UUID.fromString(resp.getString("requestId"));
                String type = resp.getString("type");

                // если полученное сообщение - ожидаемый ответ на один из запросов
                if (pendingResponses.containsKey(requestId))
                {
                    handleResponse(jsonResponse, type);
                }
                // иначе - это команда/сообщение от сервера
                else
                {
                    handleServerMessage(jsonResponse, type);
                }
            }

        } catch (IOException i) {
            System.out.println(i.toString());
        }
    }

    public void handleResponse(String jsonResponse, String type)
    {
        Response response = null;
        try {
            switch (type) {
                case "GetUser":
                    response = jsonMapper.readValue(jsonResponse, GetUserResponse.class);
                    break;
                case "GetEmails":
                    response = jsonMapper.readValue(jsonResponse, GetEmailsResponse.class);
                    break;
            }
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            throw new RuntimeException(e.toString());
        }

        if (response == null)
            return;
        var id = response.getRequestId();
        var future = pendingResponses.remove(id);
        System.out.println("response with id " + id + " complete");
        future.complete(response);
    }

    public void handleServerMessage(String jsonMessage, String type)
    {

    }

    public void newEmailHandler(EmailDTO email)
    {

    }
}
